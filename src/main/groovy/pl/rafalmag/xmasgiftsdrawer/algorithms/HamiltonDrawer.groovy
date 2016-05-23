package pl.rafalmag.xmasgiftsdrawer.algorithms

import com.google.common.base.Preconditions
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import pl.rafalmag.xmasgiftsdrawer.GiversReceivers
import pl.rafalmag.xmasgiftsdrawer.GiversReceiversFactory
import pl.rafalmag.xmasgiftsdrawer.Model
import pl.rafalmag.xmasgiftsdrawer.Person
import pl.rafalmag.xmasgiftsdrawer.graph.HamiltonBacktrack2
import pl.rafalmag.xmasgiftsdrawer.graph.ModelToModelIslands2

import java.util.concurrent.*

@ToString
@EqualsAndHashCode
class HamiltonDrawer implements Drawer {

    final Model model;
    final private Random random;

    public HamiltonDrawer(Model model, Random random = new Random()) {
        Preconditions.checkArgument(model.isValid(), "model %s is invalid", model)
        this.model = model
        this.random = random
    }

    GiversReceivers draw(long timeout = 5, TimeUnit timeUnit = TimeUnit.SECONDS) throws TimeoutException, InterruptedException {
        def executor = Executors.newSingleThreadExecutor();
        try {
            Future<GiversReceivers> future = executor.submit(new Callable() {
                @Override
                GiversReceivers call() throws Exception {
                    drawInternal()
                }
            })
            future.get(timeout, timeUnit);
        } finally {
            executor.shutdownNow()
        }
    }

    GiversReceivers drawInternal() {
        def models = new ModelToModelIslands2().modelToModelIslands(model)
        List<List<Person>> persons = models.collect {
            new HamiltonBacktrack2(random).getHamiltonCycle(it)
        }
        GiversReceiversFactory.fromPersonLists(persons)
    }

}
