package com.sawacorp.mytime.view.mainScreen

import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sawacorp.mytime.R
import com.sawacorp.mytime.getDateToInt
import com.sawacorp.mytime.model.PieChartData
import com.sawacorp.mytime.service.TimerService
import com.sawacorp.mytime.ui.theme.*
import com.sawacorp.mytime.view.NewTaskElement
import com.sawacorp.mytime.view.PieChart
import com.sawacorp.mytime.view.PopUpPeriod

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel()
) {

    var showPopUp by remember {
        mutableStateOf(false)
    }
    var showNewTaskElement by remember {
        mutableStateOf(false)
    }
    val period by viewModel.period.observeAsState("Сегодня")

    var sliceForEdit: PieChartData.Slice? by remember {
        mutableStateOf(null)
    }

    val context = LocalContext.current
    val serviceIntent by remember {
        mutableStateOf(Intent(context.applicationContext, TimerService::class.java))
    }
    context.registerReceiver(viewModel.updateTime, IntentFilter(TimerService.TIMER_UPDATED))

    viewModel.allSlice.observe(LocalLifecycleOwner.current) {
        viewModel.setPeriod(viewModel.period.value ?: "Сегодня")
    }
    val listSlice by viewModel.sliceByDate.observeAsState(listOf())

    val activeSlice by viewModel.activeSlice.observeAsState(null)
    val activeTime by viewModel.timeInString.observeAsState("")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Main)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        )
        Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
            Card(
                shape = CircleShape,
                contentColor = Color.White
            ) {
                PieChart(
                    modifier = Modifier.padding(15.dp),
                    pieChartData = PieChartData(slices = listSlice.ifEmpty {
                        listOf(
                            PieChartData.Slice(
                                name = "",
                                value = 0F,
                                color = Color.Green.toArgb(),
                                date = getDateToInt()
                            )
                        )
                    })
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (activeSlice != null) {
                    Text(
                        text = activeSlice?.name.toString(),
                        modifier = Modifier.padding(2.dp),
                        style = styleTypeOne
                    )
                    Text(
                        text = activeTime,
                        modifier = Modifier.padding(2.dp),
                        style = styleTypeTwo,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = "pause", modifier = Modifier.clickable {
                            context.stopService(serviceIntent)
                            viewModel.stopTimer(activeSlice?.id ?: 0)
                        }
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Мои Активности",
                    style = styleTypeThree
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        showPopUp = !showPopUp
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "add"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = period,
                        style = styleTypeOne,
                        color = Blue80,
                        fontFamily = FontFamily(Font(R.font.gotham_medium)),
                        lineHeight = 19.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "add", modifier = Modifier.clickable {
                    showNewTaskElement = !showNewTaskElement
                }
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            contentColor = Color.White
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
            ) {
                listSlice.forEach { item ->
                    item {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .size(width = 5.dp, height = 40.dp)
                                        .background(Color(item.color))
                                )
                                Text(
                                    modifier = Modifier.padding(start = 10.dp),
                                    text = item.name,
                                    style = itemStyleText
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (item.id == viewModel.activeSlice.value?.id)
                                        activeTime else viewModel.getTimeStringFromDouble(
                                        item.value.toDouble()
                                    ),
                                    style = itemStyleTime
                                )
                                Row(
                                    modifier = Modifier.width(70.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_change),
                                        contentDescription = "change",
                                        modifier = Modifier
                                            .height(25.dp)
                                            .width(20.dp)
                                            .clickable {
                                                sliceForEdit = item
                                                showNewTaskElement = !showNewTaskElement
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    if (item.id == viewModel.activeSlice.value?.id) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_pause),
                                            contentDescription = "pause",
                                            modifier = Modifier
                                                .height(25.dp)
                                                .width(20.dp)
                                                .clickable {
                                                    context.stopService(serviceIntent)
                                                    viewModel.stopTimer(item.id)
                                                }
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_play),
                                            contentDescription = "play",
                                            modifier = Modifier
                                                .height(25.dp)
                                                .width(20.dp)
                                                .clickable {
                                                    if (viewModel.activeSlice.value != null) {
                                                        context.stopService(serviceIntent)
                                                        viewModel.stopTimer(
                                                            viewModel.activeSlice.value?.id
                                                                ?: item.id
                                                        )
                                                    }

                                                    viewModel.startTimer(item)
                                                    serviceIntent.putExtra(
                                                        TimerService.TIME_EXTRA,
                                                        viewModel.time.value
                                                    )
                                                    context.startService(serviceIntent)
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showPopUp,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        PopUpPeriod(
            { newPeriod ->
                viewModel.setPeriod(newPeriod)
                showPopUp = !showPopUp
            }, {
                showPopUp = !showPopUp
            }
        )
    }

    AnimatedVisibility(
        visible = showNewTaskElement,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        NewTaskElement(
            sliceForEdit,
            { nameTask, colorTask ->
                if (sliceForEdit == null) {
                    viewModel.addTask(
                        PieChartData.Slice(
                            name = nameTask,
                            value = 0F,
                            color = colorTask.toArgb(),
                            date = getDateToInt()
                        )
                    )
                } else {
                    sliceForEdit?.let { viewModel.editTask(it, nameTask, colorTask) }
                    sliceForEdit = null
                }
                showNewTaskElement = !showNewTaskElement
            }, { showNewTaskElement = !showNewTaskElement },
            {
                sliceForEdit?.let { viewModel.deleteTask(it) }
                sliceForEdit = null
                showNewTaskElement = !showNewTaskElement
            })
    }

}