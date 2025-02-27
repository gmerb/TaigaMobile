package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.*

interface ITasksRepository {
    suspend fun getWorkingOn(): List<CommonTask>
    suspend fun getWatching(): List<CommonTask>

    suspend fun getStatuses(commonTaskType: CommonTaskType): List<Status>
    suspend fun getStatusByType(commonTaskType: CommonTaskType, statusType: StatusType): List<Status>

    suspend fun getSprints(page: Int): List<Sprint>

    suspend fun getEpics(page: Int, query: String? = null): List<CommonTask>

    suspend fun getAllUserStories(): List<CommonTaskExtended> // for stories kanban
    suspend fun getBacklogUserStories(page: Int, query: String): List<CommonTask>
    suspend fun getSprintUserStories(sprintId: Long): List<CommonTask>
    suspend fun getEpicUserStories(epicId: Long): List<CommonTask>

    suspend fun getUserStoryTasks(storyId: Long): List<CommonTask>
    suspend fun getSprintTasks(sprintId: Long): List<CommonTask>

    suspend fun getIssues(page: Int, query: String): List<CommonTask>
    suspend fun getSprintIssues(sprintId: Long): List<CommonTask>

    suspend fun getCommonTask(commonTaskId: Long, type: CommonTaskType): CommonTaskExtended

    suspend fun getComments(commonTaskId: Long, type: CommonTaskType): List<Comment>

    // edit related
    suspend fun changeStatus(commonTaskId: Long, commonTaskType: CommonTaskType, statusId: Long, statusType: StatusType, version: Int)
    suspend fun changeSprint(commonTaskId: Long, commonTaskType: CommonTaskType, sprintId: Long?, version: Int)
    suspend fun linkToEpic(epicId: Long, userStoryId: Long)
    suspend fun unlinkFromEpic(epicId: Long, userStoryId: Long)
    suspend fun changeAssignees(commonTaskId: Long, commonTaskType: CommonTaskType, assignees: List<Long>, version: Int)
    suspend fun changeWatchers(commonTaskId: Long, commonTaskType: CommonTaskType, watchers: List<Long>, version: Int)
    suspend fun createComment(commonTaskId: Long, commonTaskType: CommonTaskType, comment: String, version: Int)
    suspend fun deleteComment(commonTaskId: Long, commonTaskType: CommonTaskType, commentId: String)
    suspend fun editTask(commonTaskId: Long, commonTaskType: CommonTaskType, title: String, description: String, version: Int)

    suspend fun createCommonTask(
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        parentId: Long? = null,
        sprintId: Long? = null,
        statusId: Long? = null
    ): CommonTask

    suspend fun deleteCommonTask(commonTaskType: CommonTaskType, commonTaskId: Long)

    suspend fun promoteCommonTaskToUserStory(commonTaskId: Long, commonTaskType: CommonTaskType): CommonTask
}