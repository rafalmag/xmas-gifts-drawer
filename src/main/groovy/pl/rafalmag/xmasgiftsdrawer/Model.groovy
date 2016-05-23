package pl.rafalmag.xmasgiftsdrawer

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table

class Model {

    private final Table<Person /*giver*/, Person/*receiver*/, Boolean> table
    private final IModelValidator validator

    public Model(List<Person> limitedPersons, Model model) {
        table = HashBasedTable.create(limitedPersons.size(), limitedPersons.size())
        this.validator = model.validator
        limitedPersons.each { giver ->
            limitedPersons.each { receiver ->
                table.put(giver, receiver, model.canGive(giver, receiver))
            }
        }
    }

    public Model(List<Person> persons = [], IModelValidator modelValidator = new ModelValidator()) {
        table = HashBasedTable.create(persons.size(), persons.size())
        this.validator = modelValidator
        initTable(persons)
    }

    private void initTable(List<Person> persons) {
        persons.each { giver ->
            persons.each { receiver ->
                if (giver.equals(receiver)) {
                    setCannotGive(giver, receiver)
                } else {
                    setCanGive(giver, receiver)
                }
            }
        }
    }

    public boolean canGive(Person giver, Person receiver) {
        table.get(giver, receiver)
    }

    public Set<Person> getPersons() {
        def persons = [] as SortedSet
        persons.addAll(table.rowKeySet())
        persons.addAll(table.columnKeySet())
        persons
    }

    /**
     *
     * @param giver
     * @param receiver
     * @return self , so it can be easily chained to build model as one liner
     */
    public Model setCanGive(Person giver, Person receiver) {
        table.put(giver, receiver, true)
        this
    }

    /**
     *
     * @param giver
     * @param receiver
     * @return self , so it can be easily chained to build model as one liner
     */
    public Model setCannotGive(Person giver, Person receiver) {
        table.put(giver, receiver, false)
        this
    }

    public boolean isValid() {
        giversMatchesReceivers() && validator.isValid(this)
    }

    public boolean giversMatchesReceivers() {
        table.rowKeySet() == table.columnKeySet()
    }

    public String toString() {
        String toString = " ;"
        getPersons().each {
            toString += it.toString() + ";"
        }
        toString += "\n"
        getPersons().each { giver ->
            toString += giver.toString() + ";"
            getPersons().each { receiver ->
                def canGive = canGive(giver, receiver) ? "1" : "0"
                toString += canGive + ";"
            }
            toString += "\n"
        }
        toString
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Model model = (Model) o

        if (table != model.table) return false

        return true
    }

    int hashCode() {
        return (table != null ? table.hashCode() : 0)
    }
}
