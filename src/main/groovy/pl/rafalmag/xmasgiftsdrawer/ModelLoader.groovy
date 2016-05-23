package pl.rafalmag.xmasgiftsdrawer

import com.google.common.base.Splitter
import com.google.common.collect.Lists
import groovy.stream.Stream

class ModelLoader {

    private final InputStreamReader reader

    ModelLoader(InputStream inputStream) {
        this.reader = new InputStreamReader(inputStream)
    }

    Model load() {
        List<Person> persons = parseHeader()
        def model = new Model([])
        parseValues(model, persons)
        if(!model.giversMatchesReceivers()){
            throw new ModelLoaderException("Givers do not match receivers (too many columns?) in model: $model")
        }
        model
    }

    def List<Person> parseHeader() {
        def header = readLine()
        Iterable<String> names = Splitter.on(';').trimResults().omitEmptyStrings().split(header)
        names.collect { name -> new Person(name) }
    }

    private String readLine() {
        String line = ""
        while (line?.isEmpty()) {
            line = reader.readLine()
        }
        line
    }

    def parseValues(Model model, List<Person> persons) {
        Stream.from(reader.readLines())
                .filter { !it.empty }
                .toList()
                .each { parseLine(model, persons, it) }
    }

    def static void parseLine(Model model, List<Person> receivers, String line) {
        def values = Lists.newLinkedList(Splitter.on(';').trimResults().split(line))
        def giverValue = values.removeFirst()
        Person giver = new Person(giverValue)
        receivers.each { receiver ->
            def value = values.removeFirst()
            switch (value) {
                case '1':
                    model.setCanGive(giver, receiver)
                    break;
                case '0':
                    model.setCannotGive(giver, receiver)
                    break;
                default:
                    //TODO my exception + test
                    throw new IOException("Value: " + value + " in line: " + line + " not supported.")
            }
        }
    }

    static class ModelLoaderException extends Exception{
        ModelLoaderException(String message) {
            super(message)
        }

        ModelLoaderException(String message, Throwable cause) {
            super(message, cause)
        }
    }
}

