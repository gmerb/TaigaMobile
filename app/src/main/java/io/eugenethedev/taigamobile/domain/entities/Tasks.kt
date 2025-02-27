package io.eugenethedev.taigamobile.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Tasks related entities
 */

data class Status(
    val id: Long,
    val name: String,
    val color: String,
    val type: StatusType
)

enum class StatusType {
    Status,
    Type,
    Severity,
    Priority
}

@Parcelize
data class Sprint(
    val id: Long,
    val name: String,
    val order: Int,
    val start: Date,
    val finish: Date,
    val storiesCount: Int,
    val isClosed: Boolean
) : Parcelable


enum class CommonTaskType {
    UserStory,
    Task,
    Epic,
    Issue
}


data class CommonTask(
    val id: Long,
    val createdDate: Date,
    val title: String,
    val ref: Int,
    val status: Status,
    val assignee: User? = null,
    val projectInfo: Project,
    val taskType: CommonTaskType,
    val isClosed: Boolean,
    val colors: List<String> = emptyList() // colored indicators (for stories and epics)
)


data class CommonTaskExtended(
    val id: Long,
    val status: Status,
    val createdDateTime: Date,
    val sprint: Sprint?,
    val assignedIds: List<Long>,
    val watcherIds: List<Long>,
    val creatorId: Long,
    val ref: Int,
    val title: String,
    val isClosed: Boolean,
    val description: String,
    val epicsShortInfo: List<EpicShortInfo>,
    val projectSlug: String,
    val userStoryShortInfo: UserStoryShortInfo? = null,
    val version: Int,
    val color: String? = null, // for epic
    // for issue
    val type: Status? = null,
    val priority: Status? = null,
    val severity: Status? = null
)


data class EpicShortInfo(
    val id: Long,
    @SerializedName("subject") val title: String,
    val ref: Int,
    val color: String
)


data class UserStoryShortInfo(
    val id: Long,
    val ref: Int,
    @SerializedName("subject") val title: String,
    val epics: List<EpicShortInfo>?
) {
    val epicColors get() = epics?.map { it.color }.orEmpty()
}

data class Comment(
    val id: String,
    @SerializedName("user") val author: User,
    @SerializedName("comment") val text: String,
    @SerializedName("created_at") val postDateTime: Date,
    @SerializedName("delete_comment_date") val deleteDate: Date?
) {
    var canDelete: Boolean? = null
}