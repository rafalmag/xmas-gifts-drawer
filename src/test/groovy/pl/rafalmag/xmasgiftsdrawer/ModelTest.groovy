package pl.rafalmag.xmasgiftsdrawer

import spock.lang.Specification


class ModelTest extends Specification {

    def "should allow to init model using receivers and setters"() {
        def a = new Person("A")
        def b = new Person("B")
        def c = new Person("C")
        Model model = new Model()

        when:
        model.setCanGive(a, b);
        then:
        model.canGive(a, b)
        !model.canGive(a, a)
        !model.canGive(b, a)
        !model.canGive(b, b)
        model.getPersons().containsAll([a, b])
        model.toString().equals(
                " ;A;B;\n" +
                        "A;0;1;\n" +
                        "B;0;0;\n")

        when:
        model.setCanGive(b, c)
        then:
        model.canGive(a, b)
        model.canGive(b, c)
        model.getPersons().containsAll([a, b, c])
        model.toString().equals(
                " ;A;B;C;\n" +
                        "A;0;1;0;\n" +
                        "B;0;0;1;\n" +
                        "C;0;0;0;\n")
    }

    def "should models with same persons, same canGive values equals"(){
        given:
        def a = new Person("A")
        def b = new Person("B")
        def c = new Person("C")
        Model model = new Model().setCanGive(a,b).setCanGive(b,c)
        Model model2 = new Model().setCanGive(a,b).setCanGive(b,c)
        expect:
        model == model2
    }

    def "should models with same persons, but different canGive values differ"(){
        given:
        def a = new Person("A")
        def b = new Person("B")
        def c = new Person("C")
        Model model = new Model().setCanGive(a,b).setCanGive(b,c)
        Model model2 = new Model().setCanGive(b,a).setCanGive(c,b)
        expect:
        model != model2
    }

    def "should addPerson work"() {
        given:
        def a = new Person("A")
        Model model = new Model()
        when:
        model.addPerson(a)
        then:
        model.getPersons().size() == 1
        model.getPersons().contains(a)
    }

    def "should add person init table"() {
        def a = new Person("A")
        def b = new Person("B")
        Model model = new Model()
        when:
        model.addPerson(a)
        model.addPerson(b)
        then:
        model.canGive(a, b)
        !model.canGive(a, a)
        model.canGive(b, a)
        !model.canGive(b, b)
        model.getPersons().containsAll([a, b])
    }

    def "should remove person work"() {
        def a = new Person("A")
        Model model = new Model([a])
        when:
        model.removePerson(a)
        then:
        model.getPersons().isEmpty()
    }

    def "should remove person work2"() {
        def a = new Person("A")
        def b = new Person("B")
        Model model = new Model([a, b])
        when:
        model.removePerson(a)
        then:
        model.getPersons().size() == 1
        model.getPersons().contains(b)
        !model.canGive(a, a)
        !model.canGive(a, b)
        !model.canGive(b, a)
        !model.canGive(b, b)
    }

    def "should remove already removed person"() {
        def toBeRemovedTwice = new Person("A")
        def neverRemoved = new Person("B")
        def neverPartOfModel = new Person("C")
        Model model = new Model([toBeRemovedTwice, neverRemoved])
        when:
        model.removePerson(toBeRemovedTwice)
        model.removePerson(toBeRemovedTwice)
        model.removePerson(neverPartOfModel)
        then:
        model.getPersons().size() == 1
        model.getPersons().contains(neverRemoved)
    }
}
