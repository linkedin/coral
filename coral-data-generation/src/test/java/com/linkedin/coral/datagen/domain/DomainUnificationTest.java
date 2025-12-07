package com.linkedin.coral.datagen.domain;

import java.util.List;

import org.testng.annotations.Test;

/**
 * Tests demonstrating the unified Domain API across IntegerDomain and RegexDomain.
 */
public class DomainUnificationTest {

    @Test
    public void testIntegerDomainUnifiedApi() {
        System.out.println("\n=== Integer Domain - Unified API ===");
        
        // Create domains
        IntegerDomain domain1 = IntegerDomain.of(1, 10);
        IntegerDomain domain2 = IntegerDomain.of(5, 15);
        
        System.out.println("Domain 1: " + domain1);
        System.out.println("Domain 2: " + domain2);
        
        // Test isEmpty
        System.out.println("Domain 1 is empty: " + domain1.isEmpty());
        
        // Test intersect
        IntegerDomain intersection = domain1.intersect(domain2);
        System.out.println("Intersection: " + intersection);
        
        // Test union
        IntegerDomain union = domain1.union(domain2);
        System.out.println("Union: " + union);
        
        // Test sample (unified API)
        List<Long> samples = intersection.sample(5);
        System.out.println("Samples from intersection: " + samples);
        
        // Test isSingleton
        IntegerDomain singleton = IntegerDomain.of(42);
        System.out.println("Singleton domain: " + singleton);
        System.out.println("Is singleton: " + singleton.isSingleton());
        System.out.println("Is domain1 singleton: " + domain1.isSingleton());
    }

    @Test
    public void testRegexDomainUnifiedApi() {
        System.out.println("\n=== Regex Domain - Unified API ===");
        
        // Create domains
        RegexDomain domain1 = new RegexDomain("a.*");  // starts with 'a'
        RegexDomain domain2 = new RegexDomain(".*b");  // ends with 'b'
        
        System.out.println("Domain 1: " + domain1);
        System.out.println("Domain 2: " + domain2);
        
        // Test isEmpty
        System.out.println("Domain 1 is empty: " + domain1.isEmpty());
        
        // Test intersect
        RegexDomain intersection = domain1.intersect(domain2);
        System.out.println("Intersection: " + intersection);
        
        // Test union
        RegexDomain union = domain1.union(domain2);
        System.out.println("Union: " + union);
        
        // Test sample (unified API)
        List<String> samples = intersection.sample(5);
        System.out.println("Samples from intersection: " + samples);
        
        // Test isSingleton
        RegexDomain singleton = RegexDomain.literal("hello");
        System.out.println("Singleton domain: " + singleton);
        System.out.println("Is singleton: " + singleton.isSingleton());
        System.out.println("Is domain1 singleton: " + domain1.isSingleton());
    }

    @Test
    public void testEmptyDomains() {
        System.out.println("\n=== Empty Domains ===");
        
        // Integer empty domain
        IntegerDomain intEmpty = IntegerDomain.empty();
        System.out.println("Integer empty: " + intEmpty);
        System.out.println("Is empty: " + intEmpty.isEmpty());
        System.out.println("Samples: " + intEmpty.sample(5));
        
        // Regex empty domain
        RegexDomain regexEmpty = RegexDomain.empty();
        System.out.println("\nRegex empty: " + regexEmpty);
        System.out.println("Is empty: " + regexEmpty.isEmpty());
        System.out.println("Samples: " + regexEmpty.sample(5));
    }

    @Test
    public void testIntersectionBecomesEmpty() {
        System.out.println("\n=== Intersection Becomes Empty ===");
        
        // Integer: disjoint intervals
        IntegerDomain int1 = IntegerDomain.of(1, 10);
        IntegerDomain int2 = IntegerDomain.of(20, 30);
        IntegerDomain intIntersection = int1.intersect(int2);
        System.out.println("Integer domain 1: " + int1);
        System.out.println("Integer domain 2: " + int2);
        System.out.println("Integer intersection: " + intIntersection);
        System.out.println("Is empty: " + intIntersection.isEmpty());
        
        // Regex: contradictory patterns
        RegexDomain regex1 = new RegexDomain("a+");   // one or more 'a'
        RegexDomain regex2 = new RegexDomain("b+");   // one or more 'b'
        RegexDomain regexIntersection = regex1.intersect(regex2);
        System.out.println("\nRegex domain 1: " + regex1);
        System.out.println("Regex domain 2: " + regex2);
        System.out.println("Regex intersection: " + regexIntersection);
        System.out.println("Is empty: " + regexIntersection.isEmpty());
    }

    @Test
    public void testPolymorphicUsage() {
        System.out.println("\n=== Polymorphic Usage ===");
        
        // Can use Domain reference for both types
        Domain<Long, IntegerDomain> intDomain = IntegerDomain.of(1, 10);
        Domain<String, RegexDomain> strDomain = new RegexDomain("[0-9]+");
        
        System.out.println("Integer domain isEmpty: " + intDomain.isEmpty());
        System.out.println("String domain isEmpty: " + strDomain.isEmpty());
        
        System.out.println("Integer samples: " + intDomain.sample(3));
        System.out.println("String samples: " + strDomain.sample(3));
    }

    @Test
    public void testChainedOperations() {
        System.out.println("\n=== Chained Operations ===");
        
        // Integer: chain intersections
        IntegerDomain result = IntegerDomain.of(1, 100)
            .intersect(IntegerDomain.of(10, 50))
            .intersect(IntegerDomain.of(20, 30));
        System.out.println("Integer chained intersections: " + result);
        System.out.println("Samples: " + result.sample(5));
        
        // Regex: chain operations
        RegexDomain regexResult = new RegexDomain(".*")
            .intersect(new RegexDomain("[a-z]+"))
            .intersect(new RegexDomain("a.*"));
        System.out.println("\nRegex chained intersections: " + regexResult);
        System.out.println("Samples: " + regexResult.sample(5));
    }

    @Test
    public void testSingletonDetection() {
        System.out.println("\n=== Singleton Detection ===");
        
        // Integer singletons
        IntegerDomain intSingle = IntegerDomain.of(42);
        IntegerDomain intRange = IntegerDomain.of(1, 10);
        System.out.println("Integer {42} is singleton: " + intSingle.isSingleton());
        System.out.println("Integer [1,10] is singleton: " + intRange.isSingleton());
        
        // Regex singletons
        RegexDomain regexSingle = RegexDomain.literal("hello");
        RegexDomain regexPattern = new RegexDomain("hel+o");
        System.out.println("Regex 'hello' is singleton: " + regexSingle.isSingleton());
        System.out.println("Regex 'hel+o' is singleton: " + regexPattern.isSingleton());
    }
}
