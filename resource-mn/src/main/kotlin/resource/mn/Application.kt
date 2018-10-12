package resource.mn

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("resource.mn")
                .mainClass(Application.javaClass)
                .start()
    }
}