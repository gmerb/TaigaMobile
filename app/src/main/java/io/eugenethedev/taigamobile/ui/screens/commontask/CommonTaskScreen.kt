package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.components.*
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalAnimationApi
@Composable
fun CommonTaskScreen(
    navController: NavController,
    commonTaskId: Long,
    commonTaskType: CommonTaskType,
    ref: Int,
    projectSlug: String,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: CommonTaskViewModel = viewModel()
    remember {
        viewModel.start(commonTaskId, commonTaskType)
        null
    }

    val story by viewModel.story.observeAsState()
    story?.subscribeOnError(onError)
    val creator by viewModel.creator.observeAsState()
    creator?.subscribeOnError(onError)
    val assignees by viewModel.assignees.observeAsState()
    assignees?.subscribeOnError(onError)
    val watchers by viewModel.watchers.observeAsState()
    watchers?.subscribeOnError(onError)
    val tasks by viewModel.tasks.observeAsState()
    tasks?.subscribeOnError(onError)
    val comments by viewModel.comments.observeAsState()
    comments?.subscribeOnError(onError)

    val statuses by viewModel.statuses.observeAsState()
    statuses?.subscribeOnError(onError)
    val statusSelectResult by viewModel.statusSelectResult.observeAsState()
    statusSelectResult?.subscribeOnError(onError)

    val sprints by viewModel.sprints.observeAsState()
    sprints?.subscribeOnError(onError)
    val sprintSelectResult by viewModel.sprintSelectResult.observeAsState()
    sprintSelectResult?.subscribeOnError(onError)

    val team by viewModel.team.observeAsState()
    team?.subscribeOnError(onError)

    story?.data.let {
        CommonTaskScreenContent(
            commonTaskType = commonTaskType,
            toolbarTitle = stringResource(
                when (commonTaskType) {
                    CommonTaskType.USERSTORY -> R.string.userstory_slug
                    CommonTaskType.TASK -> R.string.task_slug
                }
            ).format(ref),
            statusName = it?.status?.name ?: "",
            statusColorHex = it?.status?.color ?: "#000000",
            sprintName = it?.sprint?.name,
            storyTitle = it?.title ?: "",
            story = it?.userStoryShortInfo,
            epics = it?.epics.orEmpty(),
            description = it?.description ?: "",
            creationDateTime = it?.createdDateTime ?: Date(),
            creator = creator?.data,
            assignees = assignees?.data.orEmpty(),
            watchers = watchers?.data.orEmpty(),
            tasks = tasks?.data.orEmpty(),
            comments = comments?.data.orEmpty(),
            isLoading = story?.resultStatus == ResultStatus.LOADING,
            navigateBack = navController::popBackStack,
            navigateToTask = navController::navigateToTaskScreen,
            editStatus = EditAction(
                items = statuses?.data.orEmpty(),
                loadItems = viewModel::loadStatuses,
                isItemsLoading = statuses?.resultStatus == ResultStatus.LOADING,
                selectItem = viewModel::selectStatus,
                isResultLoading = statusSelectResult?.resultStatus == ResultStatus.LOADING
            ),
            editSprint = EditAction(
                items = sprints?.data.orEmpty(),
                loadItems = viewModel::loadSprints,
                isItemsLoading = sprints?.resultStatus == ResultStatus.LOADING,
                selectItem = viewModel::selectSprint,
                isResultLoading = sprintSelectResult?.resultStatus == ResultStatus.LOADING
            ),
            editAssignees = EditAction(
                items = team?.data.orEmpty(),
                loadItems = viewModel::loadTeam,
                isItemsLoading = team?.resultStatus == ResultStatus.LOADING
            ),
            editWatchers = EditAction(
                items = team?.data.orEmpty(),
                loadItems = viewModel::loadTeam,
                isItemsLoading = team?.resultStatus == ResultStatus.LOADING
            )
        )
    }

}

@ExperimentalAnimationApi
@Composable
fun CommonTaskScreenContent(
    commonTaskType: CommonTaskType,
    toolbarTitle: String,
    statusName: String,
    statusColorHex: String,
    sprintName: String?,
    storyTitle: String,
    epics: List<Epic> = emptyList(),
    story: UserStoryShortInfo?,
    description: String,
    creationDateTime: Date,
    creator: User?,
    assignees: List<User> = emptyList(),
    watchers: List<User> = emptyList(),
    tasks: List<CommonTask> = emptyList(),
    comments: List<Comment> = emptyList(),
    isLoading: Boolean = false,
    navigateBack: () -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _, _ -> },
    editStatus: EditAction<Status> = EditAction(),
    editSprint: EditAction<Sprint?> = EditAction(),
    editAssignees: EditAction<TeamMember> = EditAction(),
    editWatchers: EditAction<TeamMember> = EditAction()
) = Column(Modifier.fillMaxSize()) {
    var isStatusSelectorVisible by remember { mutableStateOf(false) }
    var isSprintSelectorVisible by remember { mutableStateOf(false) }
    var isAssigneesSelectorVisible by remember { mutableStateOf(false) }
    var isWatchersSelectorVisible by remember { mutableStateOf(false) }

    // Bunch of list selectors
    Selectors(
        editStatus = editStatus,
        isStatusSelectorVisible = isStatusSelectorVisible,
        hideStatusSelector = { isStatusSelectorVisible = false },
        editSprint = editSprint,
        isSprintSelectorVisible = isSprintSelectorVisible,
        hideSprintSelector = { isSprintSelectorVisible = false },
        editAssignees = editAssignees,
        isAssigneesSelectorVisible = isAssigneesSelectorVisible,
        hideAssigneesSelector = { isAssigneesSelectorVisible = false },
        editWatchers = editWatchers,
        isWatchersSelectorVisible = isWatchersSelectorVisible,
        hideWatchersSelector = { isWatchersSelectorVisible = false }
    )

    AppBarWithBackButton(
        title = {
            Text(
                text = toolbarTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigateBack = navigateBack
    )

    if (isLoading || creator == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularLoader()
        }
    } else {
        val sectionsMargin = 8.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mainHorizontalScreenPadding)
        ) {

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // status
                    ClickableBadge(
                        text = statusName,
                        colorHex = statusColorHex,
                        onClick = {
                            isStatusSelectorVisible = true
                            editStatus.loadItems(null)
                        },
                        isLoading = editStatus.isResultLoading
                    )

                    Spacer(Modifier.width(8.dp))

                    // sprint
                    ClickableBadge(
                        text = sprintName ?: stringResource(R.string.no_sprint),
                        color = sprintName?.let { MaterialTheme.colors.primary } ?: Color.Gray,
                        onClick = {
                            isSprintSelectorVisible = true
                            editSprint.loadItems(null)
                        },
                        isLoading = editSprint.isResultLoading,
                        isClickable = commonTaskType != CommonTaskType.TASK
                    )

                }

                // title
                Text(
                    text = storyTitle,
                    style = MaterialTheme.typography.h5
                )

                Spacer(Modifier.height(4.dp))
            }

            // belongs to
            if (epics.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.belongs_to),
                        style = MaterialTheme.typography.subtitle1
                    )
                }

                items(epics) {
                    EpicItem(it)
                    Spacer(Modifier.height(2.dp))
                }
            }

            // belongs to (story)
            story?.let {
                item {
                    Text(
                        text = stringResource(R.string.belongs_to),
                        style = MaterialTheme.typography.subtitle1
                    )

                    UserStoryItem(story)
                }
            }

            item {
                Spacer(Modifier.height(sectionsMargin * 2))

                // description
                if (description.isNotEmpty()) {
                    Text(description)
                } else {
                    NothingToSeeHereText()
                }

                Spacer(Modifier.height(sectionsMargin * 2))

                // created by
                Text(
                    text = stringResource(R.string.created_by),
                    style = MaterialTheme.typography.subtitle1
                )

                UserItem(
                    user = creator,
                    dateTime = creationDateTime
                )

                Spacer(Modifier.height(sectionsMargin))

                // assigned to
                Text(
                    text = stringResource(R.string.assigned_to),
                    style = MaterialTheme.typography.subtitle1
                )
            }

            items(assignees) {
                UserItem(it)
                Spacer(Modifier.height(6.dp))
            }

            // add assignee & loader
            item {
                if (editAssignees.isResultLoading) {
                    DotsLoader()
                }
                AddUserItem(
                    onClick = {
                        isAssigneesSelectorVisible = true
                        editAssignees.loadItems(null)
                    }
                )
            }


            item {
                Spacer(Modifier.height(sectionsMargin))

                // watchers
                Text(
                    text = stringResource(R.string.watchers),
                    style = MaterialTheme.typography.subtitle1
                )
            }

            items(watchers) {
                UserItem(it)
                Spacer(Modifier.height(6.dp))
            }

            // add watcher & loader
            item {
                if (editWatchers.isResultLoading) {
                    DotsLoader()
                }
                AddUserItem(
                    onClick = {
                        isWatchersSelectorVisible = true
                        editWatchers.loadItems(null)
                    }
                )
            }

            if (tasks.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(sectionsMargin * 2))

                    // tasks
                    Text(
                        text = stringResource(R.string.tasks),
                        style = MaterialTheme.typography.h6
                    )
                }

                itemsIndexed(tasks) { index, item ->
                    CommonTaskItem(
                        commonTask = item,
                        horizontalPadding = 0.dp,
                        navigateToTask = navigateToTask
                    )

                    if (index < tasks.lastIndex) {
                        Divider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color.LightGray
                        )
                    }
                }
            }

            item {
                Divider(
                    modifier = Modifier.padding(top = sectionsMargin * 2, bottom = sectionsMargin),
                    color = Color.LightGray,
                    thickness = 2.dp
                )

                // comments
                Text(
                    text = stringResource(R.string.comments_template).format(comments.size),
                    style = MaterialTheme.typography.h6
                )

                Spacer(Modifier.height(4.dp))
            }

            items(comments) {
                CommentItem(it)
                Spacer(Modifier.height(10.dp))
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun Selectors(
    editStatus: EditAction<Status>,
    isStatusSelectorVisible: Boolean,
    hideStatusSelector: () -> Unit,
    editSprint: EditAction<Sprint?>,
    isSprintSelectorVisible: Boolean,
    hideSprintSelector: () -> Unit,
    editAssignees: EditAction<TeamMember>,
    isAssigneesSelectorVisible: Boolean,
    hideAssigneesSelector: () -> Unit,
    editWatchers: EditAction<TeamMember>,
    isWatchersSelectorVisible: Boolean,
    hideWatchersSelector: () -> Unit
) {
    // status editor
    SelectorList(
        titleHint = stringResource(R.string.choose_status),
        items = editStatus.items,
        isVisible = isStatusSelectorVisible,
        isLoading = editStatus.isItemsLoading,
        isSearchable = false,
        loadData = editStatus.loadItems,
        navigateBack = hideStatusSelector
    ) {
        StatusItem(
            status = it,
            onClick = {
                editStatus.selectItem(it)
                hideStatusSelector()
            }
        )
    }

    // sprint editor
    SelectorList(
        titleHint = stringResource(R.string.choose_sprint),
        items = editSprint.items,
        isVisible = isSprintSelectorVisible,
        isLoading = editSprint.isItemsLoading,
        isSearchable = false,
        loadData = editSprint.loadItems,
        navigateBack = hideSprintSelector
    ) {
        SprintItem(
            sprint = it,
            onClick = {
                editSprint.selectItem(it)
                hideSprintSelector()
            }
        )
    }
    
    // assignees editor
    SelectorList(
        titleHint = stringResource(R.string.search_members),
        items = editAssignees.items,
        isVisible = isAssigneesSelectorVisible,
        isLoading = editAssignees.isItemsLoading,
        loadData = editAssignees.loadItems,
        navigateBack = hideAssigneesSelector
    ) {
        MemberItem(
            member = it,
            onClick = {
                editAssignees.selectItem(it)
                hideAssigneesSelector()
            }
        )
    }

    // watchers editor
    SelectorList(
        titleHint = stringResource(R.string.search_members),
        items = editWatchers.items,
        isVisible = isWatchersSelectorVisible,
        isLoading = editWatchers.isItemsLoading,
        loadData = editWatchers.loadItems,
        navigateBack = hideWatchersSelector
    ) {
        MemberItem(
            member = it,
            onClick = {
                editWatchers.selectItem(it)
                hideWatchersSelector()
            }
        )
    }

}

@Composable
private fun EpicItem(
    epic: Epic
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(bottom = 4.dp)
) {
    Text(
        text = stringResource(R.string.title_with_ref_pattern).format(epic.ref, epic.title),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .weight(0.9f, fill = false)
            .padding(end = 4.dp)
    )

    Text(
        text = stringResource(R.string.epic),
        style = MaterialTheme.typography.caption,
        color = Color.White,
        modifier = Modifier
            .background(
                color = Color(android.graphics.Color.parseColor(epic.color)),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 2.dp)
            .weight(0.1f, fill = false)
    )
}

@Composable
private fun UserStoryItem(
    story: UserStoryShortInfo
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(bottom = 4.dp)
) {
    Text(
        text = stringResource(R.string.title_with_ref_pattern).format(story.ref, story.title),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .weight(0.9f, fill = false)
            .padding(end = 4.dp)
    )

    story.epicColor?.let {
        Spacer(
            Modifier
                .size(12.dp)
                .background(
                    color = Color(android.graphics.Color.parseColor(it)),
                    shape = CircleShape
                )
                .weight(0.1f, fill = false)
        )
    }
}

@Composable
private fun CommentItem(
    comment: Comment
) = Column {
    UserItem(
        user = comment.author,
        dateTime = comment.postDateTime
    )

    Text(
        text = comment.text,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun StatusItem(
    status: Status,
    onClick: () -> Unit = {}
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    Text(
        text = status.name,
        color = Color(android.graphics.Color.parseColor(status.color))
    )
}

@Composable
private fun SprintItem(
    sprint: Sprint?,
    onClick: () -> Unit = {}
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    val dateFormatter = remember { SimpleDateFormat.getDateInstance() }

    sprint?.also {
        Surface(
            contentColor = if (it.isClosed) Color.Gray else MaterialTheme.colors.onSurface
        ) {
            Column {
                Text(
                    if (it.isClosed) {
                        stringResource(R.string.closed_sprint_name_template).format(it.name)
                    } else {
                        it.name
                    }
                )

                Text(
                    text = stringResource(R.string.sprint_dates_template).format(
                        dateFormatter.format(it.start),
                        dateFormatter.format(it.finish)
                    ),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    } ?: run {
        Text(
            text = stringResource(R.string.move_to_backlog),
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun MemberItem(
    member: TeamMember,
    onClick: () -> Unit = {}
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    UserItem(user = member.toUser())
}

@Composable
private fun AddUserItem(
    onClick: () -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.clickableUnindicated(onClick = onClick),
) {
    Image(
        painter = painterResource(R.drawable.ic_add),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
    )

    Text(
        text = stringResource(R.string.add_user),
        color = MaterialTheme.colors.primary
    )
}


@ExperimentalAnimationApi
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CommonTaskScreenPreview() = TaigaMobileTheme {
    CommonTaskScreenContent(
        commonTaskType = CommonTaskType.USERSTORY,
        toolbarTitle = "Userstory #99",
        statusName = "In progress",
        statusColorHex = "#729fcf",
        sprintName = "Very very very long sprint name",
        storyTitle = "Very cool and important story. Need to do this quickly",
        story = null,
        epics = List(2) {
            Epic(
                id = 1L,
                title = "Important epic",
                ref = 1,
                color = "#F2C94C"
            )
        },
        description = "Some description about this wonderful task",
        creationDateTime = Date(),
        creator = User(
            id = 0L,
            fullName = "Full Name",
            avatarUrl = null,
            username = "username"
        ),
        assignees = List(2) {
            User(
                id = 0L,
                fullName = "Full Name",
                avatarUrl = null,
                username = "username"
            )
        },
        watchers = List(2) {
            User(
                id = 0L,
                fullName = "Full Name",
                avatarUrl = null,
                username = "username"
            )
        },
        tasks = List(4) {
            CommonTask(
                id = it.toLong(),
                createdDate = Date(),
                title = "Very cool story",
                ref = 100,
                status = Status(
                    id = (0..2).random().toLong(),
                    name = "In progress",
                    color = "#729fcf"
                ),
                assignee = CommonTask.Assignee(
                    id = it.toLong(),
                    fullName = "Name Name"
                ),
                projectSlug = "000",
                taskType = CommonTaskType.USERSTORY
            )
        },
        comments = List(4) {
            Comment(
                id = "",
                author = User(
                    id = 0L,
                    fullName = "Full Name",
                    avatarUrl = null,
                    username = "username"
                ),
                text = "This is comment text",
                postDateTime = Date()
            )
        }
    )
}