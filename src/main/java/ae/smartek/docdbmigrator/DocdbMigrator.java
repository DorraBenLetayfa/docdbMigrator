package ae.smartek.docdbmigrator;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
import ae.smartek.docdbmigrator.service.DocdbMigratorService;

/**
 *
 * @author houssem
 */
public class DocdbMigrator {

    public static void main(String[] args) {

        DocdbMigratorService.startMigration();

    }
}
