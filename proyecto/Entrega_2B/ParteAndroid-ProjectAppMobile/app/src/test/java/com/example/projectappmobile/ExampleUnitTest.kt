package com.example.projectappmobile

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun queue_collection() {
        val queue = LinkedList<Float>();
        val max = 3;

        var i=0

        while (i < 5){
            val num =i.toFloat()

            println(queue.toList())

            if (queue.size == max){
                println("Se saca: " + queue.pop())
                println("Se agrega: " + num)

            }

            queue.add(num);


            i++;
        }

        assertEquals(max, queue.size)


    }


}