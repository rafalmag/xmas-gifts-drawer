package pl.rafalmag.xmasgiftsdrawer

import groovy.transform.Canonical

//TODO model can be simplified when "only possible pairs" found - eg.
// in 4 persons model if A person can give gift only to B person, model can be simplified to 3 "persons"
@Canonical
class PersonPair extends Person {

    private final Person giver
    private final Person receiver

    public PersonPair(Person giver, Person receiver) {
        super(giver.toString() + "->" + receiver.toString())
        this.receiver = receiver
        this.giver = giver
    }

}
