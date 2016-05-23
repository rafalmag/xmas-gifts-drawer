package pl.rafalmag.xmasgiftsdrawer

import spock.lang.Shared
import spock.lang.Specification;

public class ModelLoaderTest extends Specification {

    @Shared
    def a = new Person("A")
    @Shared
    def b = new Person("B")
    @Shared
    def c = new Person("C")
    @Shared
    def d = new Person("D")

    Model loadModelFromResource(String resourceName) {
        InputStream is = null
        try {
            is = ModelLoaderTest.class.getResourceAsStream(resourceName)
            def modelLoader = new ModelLoader(is)
            modelLoader.load()
        } finally {
            is?.close()
        }
    }

    def "should contain proper names"() {
        when:
        Model model = loadModelFromResource("/model.csv")
        def persons = model.getPersons()
        then:
        persons.containsAll([a, b, c, d])
    }

    def "should read can buy fields"() {
        when:
        Model model = loadModelFromResource("/model.csv")
        then:
        !model.canGive(a, a) // seems obvious
        model.canGive(a, b)
        model.canGive(a, c)
        model.canGive(a, d)

        model.canGive(b, a)
        !model.canGive(b, b) // seems obvious
        !model.canGive(b, c) // special rule in model.csv
        model.canGive(b, d)

        model.canGive(c, a)
        model.canGive(c, b)
        !model.canGive(c, c) // seems obvious
        model.canGive(c, d)

        model.canGive(d, a)
        model.canGive(d, b)
        model.canGive(d, c)
        !model.canGive(d, d)  // seems obvious
    }

    def "should parse line"() {
        given:
        def receivers = [a, b, c, d]
        Model model = new Model()
        def giver = a

        when:
        ModelLoader.parseLine(model, receivers, "A;0;1;0;1")

        then:
        !model.canGive(giver, a)
        model.canGive(giver, b)
        !model.canGive(giver, c)
        model.canGive(giver, d)
    }

    def "should load random rows order model"() {
        when:
        Model model = loadModelFromResource("/model.csv")
        Model model2 = loadModelFromResource("/modelRandomOrder.csv")
        then:
        model == model2
    }

    def "should not load model if too many columns"() {
        when:
        loadModelFromResource("/modelTooManyColumns.csv")
        then:
        ModelLoader.ModelLoaderException ex = thrown()
        ex.message =~ /(?i)givers.*receivers/
    }

    def "should not load model if too many rows"() {
        when:
        loadModelFromResource("/modelTooManyRows.csv")
        then:
        ModelLoader.ModelLoaderException ex = thrown()
        ex.message =~ /(?i)givers.*receivers/
    }

    def "should not load model if givers do not match receivers"() {
        when:
        loadModelFromResource("/modelDiffGiversAndReceivers.csv")
        then:
        ModelLoader.ModelLoaderException ex = thrown()
        ex.message =~ /(?i)givers.*receivers/
    }
}

