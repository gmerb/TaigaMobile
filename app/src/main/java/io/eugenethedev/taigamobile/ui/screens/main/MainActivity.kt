package io.eugenethedev.taigamobile.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.screens.login.LoginScreen
import io.eugenethedev.taigamobile.ui.screens.projectselector.ProjectSelectorScreen
import io.eugenethedev.taigamobile.ui.screens.scrum.ScrumScreen
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scaffoldState = rememberScaffoldState()
            val navController = rememberNavController()

            TaigaMobileTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    snackbarHost = {
                        SnackbarHost(it) {
                            Snackbar(
                                snackbarData = it,
                                backgroundColor = MaterialTheme.colors.surface,
                                contentColor = contentColorFor(MaterialTheme.colors.surface)
                            )
                        }
                    },
                    bottomBar = {
                        val items = listOf(Screen.Stories, Screen.Team)
                        val routes = items.map { it.route }
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

                        var isLaunchSingleTopShit by remember { mutableStateOf(false) }

                        // hide bottom bar for other screens
                        if (!((isLaunchSingleTopShit && currentRoute == null) || (currentRoute in routes))) {
                            return@Scaffold
                        }

                        BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
                            listOf(Screen.Stories, Screen.Team).forEach { screen ->
                                BottomNavigationItem(
                                    selectedContentColor = MaterialTheme.colors.primary,
                                    unselectedContentColor = Color.Gray,
                                    icon = { Icon(painter = painterResource(screen.iconId), contentDescription = null) },
                                    label = { Text(stringResource(screen.resourceId)) },
                                    // workaround, because launchSingleTop can cause strange effect
                                    // and make currentRoute null for start destination
                                    selected = currentRoute == screen.route || (screen.route == Routes.startDestination && currentRoute == null),
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo = navController.graph.startDestination
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            isLaunchSingleTopShit = true
                                        }
                                    }
                                )
                            }
                        }
                    },
                    bodyContent = { MainScreen(scaffoldState, it, navController) }
                )
            }
        }
    }
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, @DrawableRes val iconId: Int) {
    object Stories : Screen(Routes.scrum, R.string.scrum, R.drawable.ic_stories)
    object Team : Screen(Routes.team, R.string.team, R.drawable.ic_team)
}

object Routes {
    const val login = "login"
    const val scrum = "scrum"
    const val team = "team"
    const val projectsSelector = "projects_selector"

    const val startDestination = scrum
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    scaffoldState: ScaffoldState,
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    val viewModel: MainViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    val onError: @Composable (Int) -> Unit = { message ->
        val strMessage = stringResource(message)
        coroutineScope.launch { scaffoldState.snackbarHostState.showSnackbar(strMessage) }
    }

    Box(Modifier.fillMaxSize().padding(paddingValues)) {
        NavHost(
            navController = navController,
            startDestination = if (viewModel.isLogged) Routes.startDestination else Routes.login
        ) {
            composable(Routes.login) {
                LoginScreen(
                    navController = navController,
                    onError = onError
                )
            }

            composable(Routes.scrum) {
                ScrumScreen(
                    navController = navController,
                    onError = onError
                )
            }

            composable(Routes.team) {

            }

            composable(Routes.projectsSelector) {
                ProjectSelectorScreen(
                    navController = navController,
                    onError = onError
                )
            }

        }

    }
}