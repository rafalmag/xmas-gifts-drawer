package pl.rafalmag.xmasgiftsdrawer.algorithms

import com.google.common.base.Preconditions
import com.google.common.collect.Collections2
import groovy.stream.Stream
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import pl.rafalmag.xmasgiftsdrawer.*
import pl.rafalmag.xmasgiftsdrawer.graph.ModelToModelIslands2

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ToString
@EqualsAndHashCode
class PermutationDrawer implements Drawer {
    final Model model;
    final private Random random;

    public PermutationDrawer(Model model, Random random = new Random()) {
        Preconditions.checkArgument(model.isValid(), "model %s is invalid", model)
        this.model = model
        this.random = random
    }

    GiversReceivers draw(long timeout = 5, TimeUnit timeUnit = TimeUnit.SECONDS) throws TimeoutException, InterruptedException {
        Timeout timeoutCounter = new Timeout(timeout, timeUnit);
        def models = new ModelToModelIslands2().modelToModelIslands(model)
        List<GiversReceivers> giversReceiversForModels = models.collect {
            drawInternal(timeoutCounter, it)
        }
        giversReceiversForModels.inject { acc, val ->
            acc.pairs.addAll(val.pairs)
            acc
        }
    }

    private GiversReceivers drawInternal(timeoutCounter, Model model) {
        List<Person> persons = new ArrayList<>(model.persons)
        Collections.shuffle(persons, random);
        Collection<List<Person>> permutations = Collections2.<Person> permutations(persons);

        GiversReceivers giversReceivers = Stream.from(permutations).map {
            GiversReceiversFactory.fromPersonList(it)
        }.find {
            timeoutCounter.checkTimeout();
            it.isValid(model)
        }

        if (giversReceivers == null) {
            // TODO special exception
            throw new RuntimeException("Checked all permutations for model=${model}, could not find a solution using PermutationDrawer algorithm");
        } else {
            giversReceivers
        }
    }
}
