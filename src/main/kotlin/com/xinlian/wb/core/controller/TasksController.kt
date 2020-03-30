package com.xinlian.wb.core.controller

import com.xinlian.wb.core.entity.ErrorResponse
import com.xinlian.wb.core.entity.HttpResponse
import com.xinlian.wb.core.entity.TaskResponse
import com.xinlian.wb.core.service.task.impl.ConcurrentTasksExecutor
import com.xinlian.wb.core.service.task.impl.MockTask
import org.springframework.http.HttpStatus
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import java.util.stream.Collectors
import java.util.stream.IntStream


/**
 * @author  1111
 */
@ApiIgnore
@RestController
@RequestMapping("/tasks")
class TasksController {

    @GetMapping("/sequential")
    fun sequential(@RequestParam("task") taskDelaysInSeconds: IntArray): HttpResponse<TaskResponse> {

        val watch = StopWatch()
        watch.start()

        IntStream.of(*taskDelaysInSeconds)
                .mapToObj {
                    MockTask(it)
                }
                .forEach {
                    it.execute()
                }

        watch.stop()
        return HttpResponse(TaskResponse(watch.totalTimeSeconds))
    }

    @GetMapping("/concurrent")
    fun concurrent(@RequestParam("task") taskDelaysInSeconds: IntArray, @RequestParam("threads", required = false, defaultValue = "1") numberOfConcurrentThreads: Int): HttpResponse<TaskResponse> {

        val watch = StopWatch()
        watch.start()

        val delayedTasks = IntStream.of(*taskDelaysInSeconds)
                .mapToObj {
                    MockTask(it)
                }
                .collect(Collectors.toList())

        ConcurrentTasksExecutor(numberOfConcurrentThreads, delayedTasks).execute()

        watch.stop()
        return HttpResponse(TaskResponse(watch.totalTimeSeconds))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: IllegalArgumentException) = ErrorResponse(-2, e.message ?: "未知错误")
}
