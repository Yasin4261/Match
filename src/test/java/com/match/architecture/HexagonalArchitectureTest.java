package com.match.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Hexagonal architecture rules: domain must not depend on adapters or Spring framework.
 */
class HexagonalArchitectureTest {

    private final com.tngtech.archunit.core.domain.JavaClasses classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.match");

    @Test
    void domain_should_not_depend_on_adapters() {
        noClasses().that().resideInAPackage("com.match.domain..")
            .should().dependOnClassesThat().resideInAPackage("com.match.adapter..")
            .check(classes);
    }

    @Test
    void domain_should_not_depend_on_spring() {
        noClasses().that().resideInAPackage("com.match.domain..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..")
            .check(classes);
    }

    @Test
    void application_should_not_depend_on_adapters() {
        noClasses().that().resideInAPackage("com.match.application..")
            .should().dependOnClassesThat().resideInAPackage("com.match.adapter..")
            .check(classes);
    }
}

