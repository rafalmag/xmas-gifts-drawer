package pl.rafalmag.xmasgiftsdrawer.graph

import org.graphstream.graph.Node
import pl.rafalmag.xmasgiftsdrawer.ModelLoader
import pl.rafalmag.xmasgiftsdrawer.ModelLoaderTest
import spock.lang.Specification

class Graph2Test extends Specification {

    def "should convert model to graph"() {
        given:
        def streamToModel = ModelLoaderTest.class.getResourceAsStream("/model.csv")
        def modelLoader = new ModelLoader(streamToModel)
        def model = modelLoader.load()
        streamToModel.close()
        when:
        def graph = new Graph2(model)
        then:
        graph.graph.getNodeCount() == 4
        // check directions
        graph.graph.getEdgeIterator().every {
            Node source = it.getSourceNode()
            Node target = it.getTargetNode()
            assert model.canGive(source.getAttribute("person"), target.getAttribute("person"))
            true
        }
    }
}