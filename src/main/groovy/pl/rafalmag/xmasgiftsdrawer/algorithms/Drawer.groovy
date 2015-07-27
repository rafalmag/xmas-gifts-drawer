package pl.rafalmag.xmasgiftsdrawer.algorithms

import pl.rafalmag.xmasgiftsdrawer.GiversReceivers

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

interface Drawer {

    GiversReceivers draw() throws TimeoutException, InterruptedException

    GiversReceivers draw(long timeout, TimeUnit timeUnit) throws TimeoutException, InterruptedException

}
