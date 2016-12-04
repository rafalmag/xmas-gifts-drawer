package pl.rafalmag.xmasgiftsdrawer

import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import pl.rafalmag.xmasgiftsdrawer.algorithms.Drawer
import pl.rafalmag.xmasgiftsdrawer.algorithms.HamiltonDrawer
import pl.rafalmag.xmasgiftsdrawer.algorithms.PermutationDrawer
import pl.rafalmag.xmasgiftsdrawer.algorithms.RandomDrawer

@Slf4j
class Main {

    public static void main(String[] args) {
        Model model = getExampleModel()
        log.info("Model parsed:\n{}", model)
        if (!new ModelValidator().isValid(model)) {
            throw new Exception("Model is not valid:\n$model")
        }
        List<Drawer> drawers = [new HamiltonDrawer(model), new PermutationDrawer(model), new RandomDrawer(model)]
        drawers.each {
            log.info(it.getClass().getSimpleName() + " started")
            Stopwatch stopwatch = Stopwatch.createStarted();
            def giversAndGetters = it.draw()
            if (!giversAndGetters.isValid(model)) {
                throw new Exception("Not valid result produced")
            }
            log.info(it.getClass().getSimpleName() + " computed (in $stopwatch) result:\n$giversAndGetters")
        }
        log.info("all done")
    }

    private static Model getExampleModel() {
        InputStream streamToModel = null
        try {
            streamToModel = Main.getResourceAsStream("/model.csv")
            def modelLoader = new ModelLoader(streamToModel)
            modelLoader.load()
        } finally {
            streamToModel?.close()
        }
    }

    private static Model getAzModel() {
        def persons = ('a'..'z').collect { new Person(it as String) }
        log.debug("Model for " + persons.size())
        new Model(persons)
    }
}
