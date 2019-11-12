package ru.acurresearch.dombyta.data.common.interactor

import ga.nk2ishere.dev.utils.RxBoxRepository
import io.reactivex.Single
import ru.acurresearch.dombyta.data.common.model.Master
import ru.acurresearch.dombyta.data.common.model.Task
import ru.acurresearch.dombyta.data.network.Api
import ru.acurresearch.dombyta.data.network.model.CashBoxServerData

class TaskInteractor(
    private val box: RxBoxRepository<Task>,
    private val api: Api
) {
    fun updateTasks(
        token: CashBoxServerData
    ) = api.fetchTasks(token.authHeader)
        .flatMap {
            box.createOrUpdate(it)
                .andThen(Single.just(it))
        }

    fun getTasks() =
        box.read()

    fun takeInWork(
        token: CashBoxServerData,
        task: Task,
        assignedMaster: Master
    ) =
        Single.fromCallable {
            task.master.target = assignedMaster
            task.status = Task.TaskStatus.IN_WORK
        }.flatMap { api.syncServerTask(task, task.id.toInt(), token.authHeader) }
            .flatMap {
                box.createOrUpdate(task)
                    .andThen(Single.fromCallable { task })
            }

    fun finish(
        token: CashBoxServerData,
        task: Task
    ) =
        Single.fromCallable {
            task.status = Task.TaskStatus.COMPLETE
        }.flatMap { api.syncServerTask(task, task.id.toInt(), token.authHeader) }
            .flatMap {
                box.createOrUpdate(task)
                    .andThen(Single.fromCallable { task })
            }
}