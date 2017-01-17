/**
 * Here'll go the license
 * */

package com.anything.playground;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.util.*;
import org.apache.jena.tdb.*;
import org.apache.jena.tdb.setup.*;
import org.apache.jena.riot.*;
import org.apache.jena.graph.*;
import org.apache.jena.shared.*;

/**
 * Generates a TDB database on the default tmp path and populates it, 
 * executes a query over the dataset restricting it for a named graph, 
 * or executes a query over a named graph.
 * */
public class App {
    public static void main(String[] args) {
        String databaseDir = System.getProperty("java.io.tmpdir")
            + "jena-tdb-demo";
        if(args.length < 1 ){
            System.err.println();
            System.err.println("Usage: mvn exec:java -Dexec.mainClass="
                    +"com.anything.playground.App -Dexec.args=command");
            System.err.println();
            System.err.println("Commands: ");
            System.err.println("  init      Initialize db and load "
                    +"demo ttl files");
            System.err.println("  runquery1  Run a query over a graph");
            System.err.println("  runquery2  Run a query over all graphs");
            System.exit(1);
        }
        String command = args[0];
        System.out.println("Executing command: "+command);
        switch(command){
            // Initialize and populate the database
            case "init":
                Dataset dataset = null;
                try{
                    System.out.println("Creating TDB at "+databaseDir);
                    dataset = TDBFactory.createDataset(databaseDir);
                    Model defaultModel = RDFDataMgr.loadModel("ds-dft.ttl");
                    dataset.getDefaultModel().add(defaultModel); // ok
                    dataset.getDefaultModel().write(System.out);
                    dataset.getDefaultModel().write(System.out);
                    Model ds_ng_1_ttl= RDFDataMgr.loadModel("ds-ng-1.ttl");
                    dataset.addNamedModel("ds-ng-1.ttl", ds_ng_1_ttl);
                    dataset.getNamedModel("ds-ng-1.ttl").write(System.out);
                    Model ds_ng_2_ttl = RDFDataMgr.loadModel("ds-ng-2.ttl");
                    dataset.addNamedModel("ds-ng-2.ttl", ds_ng_2_ttl);
                    dataset.getNamedModel("ds-ng-2.ttl").write(System.out);
                    TDBFactory.release(dataset);
                }finally{
                    dataset.close();
                }
                break;
            // Run a query over the dataset but restrict it to a named graph
            case "runquery1":
                System.out.println("Run a query over a graph");
                System.out.println("Database at: "+databaseDir);
                String queryString = 
                    "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"+
                    "PREFIX : <.>\n"+
                    "         SELECT ?title\n"+
                    "         { \n"+
                    "GRAPH :ds-ng-2.ttl\n"+
                    "       { ?b dc:title ?title }\n"+
                    "         }\n";
                dataset = TDBFactory.createDataset(databaseDir);
                Query query = QueryFactory.create(queryString);
                QueryExecution qe = QueryExecutionFactory.create( 
                        query, dataset );
                qe.execSelect().forEachRemaining( r -> System.out.println(r));
                break;
            // Run a query over the dataset for all graphs
            case "runquery2":
                System.out.println("Run a query over all graphs");
                System.out.println("Database at: "+databaseDir);
                queryString = 
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
                    "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"+
                    "PREFIX : <.>\n"+
                    "   SELECT *\n"+
                    "     { \n"+
                    "         { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }\n"+
                    "     }\n";
                dataset = TDBFactory.createDataset(databaseDir);
                query = QueryFactory.create(queryString);
                qe = QueryExecutionFactory.create( 
                        query, dataset );
                qe.execSelect().forEachRemaining( r -> System.out.println(r));
                break;
        }
    }
}
