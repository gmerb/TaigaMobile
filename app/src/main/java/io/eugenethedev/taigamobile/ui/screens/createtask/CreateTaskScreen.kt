package io.eugenethedev.taigamobile.ui.screens.createtask

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.loaders.LoadingDialog
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.navigateToTaskScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@Composable
fun CreateTaskScreen(
    navController: NavController,
    commonTaskType: CommonTaskType,
    parentId: Long? = null,
    sprintId: Long? = null,
    statusId: Long? = null,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: CreateTaskViewModel = viewModel()

    val creationResult by viewModel.creationResult.observeAsState()
    creationResult?.subscribeOnError(onError)

    creationResult?.takeIf { it.resultStatus == ResultStatus.Success }?.data?.let {
        navController.popBackStack()
        navController.navigateToTaskScreen(it.id, it.taskType, it.ref)
    }

    CreateTaskScreenContent(
        title = stringResource(
            when (commonTaskType) {
                CommonTaskType.UserStory -> R.string.create_userstory
                CommonTaskType.Task -> R.string.create_task
                CommonTaskType.Epic -> R.string.create_epic
                CommonTaskType.Issue -> R.string.create_issue
            }
        ),
        isLoading = creationResult?.resultStatus == ResultStatus.Loading,
        createTask = { title, description -> viewModel.createTask(commonTaskType, title, description, parentId, sprintId, statusId) },
        navigateBack = navController::popBackStack
    )
}

@Composable
fun CreateTaskScreenContent(
    title: String,
    isLoading: Boolean = false,
    createTask: (title: String, description: String) -> Unit = { _, _ -> },
    navigateBack: () -> Unit = {}
) = Box(Modifier.fillMaxSize()) {
    TaskEditor(
        toolbarText = title,
        onSaveClick = createTask,
        navigateBack = navigateBack
    )

    if (isLoading) {
        LoadingDialog()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CreateTaskScreenPreview() = TaigaMobileTheme {
    CreateTaskScreenContent(
        title = "Create task"
    )
}