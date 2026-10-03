package com.example.hostal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SchemaFixConfig implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running SchemaFixConfig...");
        try {
            // Attempt to drop the incorrect Foreign Key constraint
            // The error log showed: CONSTRAINT `FK7q8b9ex6axbpky3g4t7lnf51y` FOREIGN KEY
            // (`user_id`) REFERENCES `user` (`id`)
            // The table `user` likely doesn't exist or is wrong (should be `users`).
            String sql = "ALTER TABLE bank_accounts DROP FOREIGN KEY FK7q8b9ex6axbpky3g4t7lnf51y";
            jdbcTemplate.execute(sql);
            System.out.println("Successfully dropped incorrect Foreign Key: FK7q8b9ex6axbpky3g4t7lnf51y");
        } catch (Exception e) {
            // Ignore if it doesn't exist (idempotent)
            System.out.println("Schema fix skipped or failed (might already be fixed): " + e.getMessage());
        }

        try {
            // Fix for my_room table error: CONSTRAINT `FKisxhhd2y0ajv1s8ony2akffsp` FOREIGN
            // KEY (`user_id`) REFERENCES `user` (`id`)
            String sql2 = "ALTER TABLE my_room DROP FOREIGN KEY FKisxhhd2y0ajv1s8ony2akffsp";
            jdbcTemplate.execute(sql2);
            System.out.println("Successfully dropped incorrect Foreign Key: FKisxhhd2y0ajv1s8ony2akffsp");
        } catch (Exception e) {
            System.out.println("Constraint FKbriupykskd2lfsmgtyr3gkmtd might not exist or already dropped.");
        }

        try {
            // Drop invalid FK on checkout table pointing to 'user' instead of 'users'
            jdbcTemplate.execute("ALTER TABLE checkout DROP FOREIGN KEY FK2t04bbekmv496a8dkcmqsknrr");
            System.out.println("Dropped invalid FK FK2t04bbekmv496a8dkcmqsknrr from checkout table");
        } catch (Exception e) {
            System.out.println("Constraint FK2t04bbekmv496a8dkcmqsknrr might not exist or already dropped.");
        }

        try {
            // Drop invalid FK on complaint table pointing to 'user' instead of 'users'
            jdbcTemplate.execute("ALTER TABLE complaint DROP FOREIGN KEY FKh8dg5n7ibjyack6pn6e71djj6");
            System.out.println("Dropped invalid FK FKh8dg5n7ibjyack6pn6e71djj6 from complaint table");
        } catch (Exception e) {
            System.out.println("Constraint FKh8dg5n7ibjyack6pn6e71djj6 might not exist or already dropped.");
        }

        try {
            // Drop invalid FK on review table pointing to 'user' instead of 'users'
            jdbcTemplate.execute("ALTER TABLE review DROP FOREIGN KEY FKiyf57dy48lyiftdrf7y87rnxi");
            System.out.println("Dropped invalid FK FKiyf57dy48lyiftdrf7y87rnxi from review table");
        } catch (Exception e) {
            System.out.println("Constraint FKiyf57dy48lyiftdrf7y87rnxi might not exist or already dropped.");
        }

        try {
            // Fix missing AUTO_INCREMENT on review table (preventative)
            jdbcTemplate.execute("ALTER TABLE review MODIFY COLUMN id INT AUTO_INCREMENT");
            System.out.println("Set AUTO_INCREMENT on review table id column");
        } catch (Exception e) {
            System.out.println("Could not set AUTO_INCREMENT on review table: " + e.getMessage());
        }

        try {
            // Fix missing AUTO_INCREMENT on complaint table
            jdbcTemplate.execute("ALTER TABLE complaint MODIFY COLUMN id INT AUTO_INCREMENT");
            System.out.println("Set AUTO_INCREMENT on complaint table id column");
        } catch (Exception e) {
            System.out.println(
                    "Could not set AUTO_INCREMENT on complaint table (might already be set): " + e.getMessage());
        }

        try {
            // Fix for payments table error: CONSTRAINT `FKbriupykskd2lfsmgtyr3gkmtd`
            // FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
            String sql3 = "ALTER TABLE payments DROP FOREIGN KEY FKbriupykskd2lfsmgtyr3gkmtd";
            jdbcTemplate.execute(sql3);
            System.out.println("Successfully dropped incorrect Foreign Key: FKbriupykskd2lfsmgtyr3gkmtd");
        } catch (Exception e) {
            System.out.println("Schema fix for payments skipped: " + e.getMessage());
        }
    }
}
