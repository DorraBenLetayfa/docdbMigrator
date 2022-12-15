/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ae.smartek.cosmosmigrator.service;

import ae.smartek.cosmosmigrator.dto.MongoDTO;

/**
 *
 * @author houssem
 */
public class CosmosMigratorService {

    public static void startMigration() {

        String databases = System.getenv("MONGODB_DATABSES");
//        String collection = System.getenv("MONGODB_COLLECTIONS");
        String namespaces = System.getenv("MONGODB_NAMESPACES");

        if ((namespaces != null) && (!namespaces.equals(""))) {
            String[] tab = namespaces.split(",");
            if (tab.length > 0) {
                for (String ns : tab) {
                    migrateSpecificNamespace(ns.trim());
                }
            }else{
                System.out.println("MONGODB_NAMESPACES entry is not valid. MONGODB_NAMESPACES should contain the namespaces (database.collection) to be migrated seperated by , ");
            }
        } else if ((databases != null) && (!databases.equals(""))) {
            String[] tab = databases.split(",");
            if (tab.length > 0) {
                for (String db : tab) {
                    migrateSpecificDatabase(db.trim());
                }

            } else {
                System.out.println("MONGODB_DATABSES entry is not valid. MONGODB_DATABSES should contain the databses names  to be migrated seperated by , ");
            }

        } else {
            migrateCluster();
        }
    }

    private static void migrateSpecificCollection(String db, String coll) {
        System.out.println("namespace: " + db + "." + coll);
        MigrationThread mt = new MigrationThread(db, coll);
        mt.start();
    }

    private static void migrateSpecificDatabase(String db) {
        MongoDTO mdto = new MongoDTO();
        mdto.getAllCollectionsInDB(db).forEach((c) -> {
            migrateSpecificCollection(db, c);
        });

    }

    private static void migrateCluster() {
        MongoDTO mdto = new MongoDTO();
        mdto.getAllDatabases().forEach((db) -> {
            migrateSpecificDatabase(db);
        });
    }

    private static void migrateSpecificNamespace(String namespace) {
        String[] tab = namespace.split("\\.");
        if (tab.length == 2) {
            migrateSpecificCollection(tab[0], tab[1]);
        } else {
            System.out.println("namespace " + namespace + " is not valid");
        }
    }

}
