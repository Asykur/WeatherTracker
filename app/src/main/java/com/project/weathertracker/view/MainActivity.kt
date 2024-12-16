package com.project.weathertracker.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.common.R
import com.project.common.utils.Constants
import com.project.common.utils.Constants.WEATHER_DATA
import com.project.common.utils.PreferenceHelper
import com.project.domain.data.WeatherDataDomain
import com.project.weathertracker.uistate.WeatherUiState
import com.project.weathertracker.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private var dataWeather by mutableStateOf(WeatherDataDomain())
    private val viewModel by viewModel<WeatherViewModel>()
    private var isCardClicked by mutableStateOf(false)
    private var showEmptyState by mutableStateOf(true)
    private var showItemCard by mutableStateOf(false)
    private var searchQuery by mutableStateOf("")

    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initObserver()
    }


    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.weatherUiSate.collect { state ->
                    when (state) {
                        is WeatherUiState.ShowLoading -> {}
                        is WeatherUiState.OnSuccess -> {
                            Log.i("DATA_WEATHER", "data = ${Gson().toJson(state.data)} ")
                            if (state.data != null) {
                                showItemCard = true
                                showEmptyState = false
                                dataWeather = state.data
                            } else {
                                showItemCard = false
                                showEmptyState = false
                            }
                        }

                        is WeatherUiState.OnError -> {
                            showItemCard = false
                            showEmptyState = false
                            Toast.makeText(this@MainActivity, "${state.message}", Toast.LENGTH_LONG).show()

                            Log.i(
                                "DATA_WEATHER",
                                "error_code = ${state.code}, message = ${state.message} "
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initViews() {
        dataWeather = WeatherDataDomain()
        preferenceHelper = PreferenceHelper(this)
        showEmptyState = preferenceHelper.getString(WEATHER_DATA).isEmpty()
        val type = object : TypeToken<WeatherDataDomain>() {}.type
        dataWeather = Gson().fromJson(preferenceHelper.getString(WEATHER_DATA), type)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    WeatherScreen()
                }
            }
        }
    }

    @Preview(name = "Weather", showBackground = true)
    @Composable
    fun WeatherScreen() {
        val showItemCard = rememberUpdatedState(newValue = showItemCard)
        val dataWeather = rememberUpdatedState(newValue = dataWeather)
        val showEmptyState = rememberUpdatedState(newValue = showEmptyState)
        val isCardClicked = rememberUpdatedState(newValue = isCardClicked)
        Log.i("STATE", "$showItemCard, $dataWeather, $showEmptyState")

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            val (containerEmpty, searchBar, cardDetail, containerResult) = createRefs()

            SearchBar(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(searchBar) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 20.dp)
            )

            EmptyState(
                Modifier
                    .constrainAs(containerEmpty) {
                        top.linkTo(searchBar.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth()
                    .height(200.dp)
                    .visible(showEmptyState.value)
                )

            CardWeather(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(containerResult) {
                        top.linkTo(searchBar.bottom, margin = 20.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .visible((showItemCard.value || showEmptyState.value.not()) && isCardClicked.value.not()),
                searchBar = searchBar,
                dataWeather = dataWeather.value
            )

            WeatherDetailInfoCard(
                modifier = Modifier
                    .constrainAs(cardDetail) {
                        top.linkTo(searchBar.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .visible(isCardClicked.value),
                dataWeather = dataWeather.value
            )

        }
    }

    @Composable
    fun EmptyState(modifier: Modifier) {
        ConstraintLayout(modifier = modifier) {
            val (tvEmptyTitle, tvEmptyDesc) = createRefs()

            Text(
                text = "No City Selected",
                modifier = Modifier.constrainAs(tvEmptyTitle) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(tvEmptyDesc.top)
                },
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.color_2c2c2c
                        )
                    ),
                    fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                    fontWeight = FontWeight.Bold
                )
            )
            val chain =
                createVerticalChain(tvEmptyTitle, tvEmptyDesc, chainStyle = ChainStyle.Packed)
            constrain(chain) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            Text(
                text = "Please Search For A City",
                modifier = Modifier
                    .constrainAs(tvEmptyDesc) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(tvEmptyTitle.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(top = 8.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.color_2c2c2c
                        )
                    ),
                    fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    @Composable
    fun SearchBar(modifier: Modifier) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        )
        val keyboardActions = KeyboardActions(
            onSearch = {
                viewModel.searchWeather(searchQuery)
                keyboardController?.hide()
            }
        )

        TextField(
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = modifier
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.color_f2f2f2
                        )
                    )
                ),
            value = searchQuery,
            onValueChange = { query ->
                isCardClicked = false
                searchQuery = query
                if (query.isEmpty()) {
                    if (preferenceHelper.getString(WEATHER_DATA).isNotEmpty()) {
                        showItemCard = true
                        showEmptyState = false
                    }else{
                        showEmptyState = true
                        showItemCard = false
                    }
                }
            },
            placeholder = {
                Text(
                    text = "Search Location", color = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.color_c4c4c4
                        )
                    ), style = TextStyle(fontFamily = FontFamily(Font(R.font.poppins_regular)))
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.color_c4c4c4
                        )
                    )
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color(0xFFF0F0F0),
                disabledContainerColor = Color(0xFFF0F0F0),
                errorContainerColor = Color(0xFFF0F0F0),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }

    @Composable
    fun CardWeather(
        modifier: Modifier,
        searchBar: ConstrainedLayoutReference,
        dataWeather: WeatherDataDomain?
    ) {
        ConstraintLayout(modifier = modifier) {
            // determine the image source
            val img = when (dataWeather?.current?.condition?.text?.lowercase()) {
                Constants.PARTLY_CLOUDY -> R.drawable.cloud_sun
                Constants.OVERCAST -> R.drawable.cloud
                Constants.SUNNY -> R.drawable.sun
                Constants.RAIN, Constants.SNOW -> "https://${dataWeather.current?.condition?.icon}"
                else -> "https://${dataWeather?.current?.condition?.icon}"
            }
            Log.i("IMG_URL", "$img")

            val (city, degree, degreeSymbol, imgWeather) = createRefs()

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                modifier = modifier
                    .clickable {
                        isCardClicked = true
                        showItemCard = false
                        preferenceHelper.setString(WEATHER_DATA, Gson().toJson(dataWeather))
                        showEmptyState = false
                    }

            ) {
                ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                    val chain =
                        createVerticalChain(city, degree, chainStyle = ChainStyle.Packed)
                    constrain(chain) {
                        top.linkTo(searchBar.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    Text(
                        text = dataWeather?.location?.name.orEmpty(),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_medium))
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .constrainAs(city) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(imgWeather.start, margin = 8.dp)
                            }
                            .fillMaxWidth(0.58f)
                            .padding(top = 16.dp, start = 25.dp)
                    )
                    Text(
                        text = dataWeather?.current?.temp_c.toString(),
                        style = TextStyle(
                            fontSize = 60.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_medium))
                        ),
                        modifier = Modifier
                            .constrainAs(degree) {
                                top.linkTo(city.bottom)
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                            }
                            .padding(start = 25.dp)
                    )
                    Text(
                        text = "°",
                        modifier = Modifier
                            .constrainAs(degreeSymbol) {
                                top.linkTo(degree.top)
                                start.linkTo(degree.end)
                            }
                            .padding(top = 10.dp),
                        style = TextStyle(fontSize = 30.sp)
                    )
                    Image(
                        painter = rememberAsyncImagePainter(img),
                        modifier = Modifier
                            .constrainAs(imgWeather) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                            .height(130.dp)
                            .width(130.dp)
                            .padding(end = 25.dp, top = 20.dp, bottom = 20.dp),
                        contentDescription = "imgResult"
                    )
                }
            }
        }
    }

    @Composable
    fun WeatherDetailInfoCard(
        modifier: Modifier = Modifier,
        dataWeather: WeatherDataDomain?
    ) {
        ConstraintLayout(modifier = modifier) {
            // determine the image source
            val img = when (dataWeather?.current?.condition?.text?.lowercase()) {
                Constants.PARTLY_CLOUDY -> R.drawable.cloud_sun
                Constants.OVERCAST -> R.drawable.cloud
                Constants.SUNNY -> R.drawable.sun
                Constants.RAIN, Constants.SNOW -> "https://${dataWeather.current?.condition?.icon}"
                else -> "https://${dataWeather?.current?.condition?.icon}"
            }
            val (degree, degreeSymbol, cardDetail, cityName, imgWeather) = createRefs()

            Image(
                modifier = Modifier
                    .constrainAs(imgWeather) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(cityName.top)
                    }
                    .width(150.dp)
                    .height(150.dp),
                painter = rememberAsyncImagePainter(img),
                contentDescription = "cloudSun",
                contentScale = ContentScale.Fit,
            )
            Text(
                modifier = Modifier
                    .constrainAs(cityName) {
                        top.linkTo(imgWeather.bottom)
                        bottom.linkTo(degree.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 16.dp),
                text = dataWeather?.location?.name.orEmpty(),
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.color_2c2c2c
                        )
                    )
                ),
                fontFamily = FontFamily(Font(R.font.poppins_semibold))
            )
            Text(
                modifier = Modifier
                    .constrainAs(degree) {
                        top.linkTo(cityName.bottom)
                        bottom.linkTo(cardDetail.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(bottom = 16.dp),
                text = String.format("%s", dataWeather?.current?.temp_c),
                style = TextStyle(
                    color = Color(
                        ContextCompat.getColor(
                            LocalContext.current, R.color.color_2c2c2c
                        )
                    ),
                    fontSize = 70.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                )
            )
            Text(
                text = "°",
                modifier = Modifier
                    .constrainAs(degreeSymbol) {
                        top.linkTo(degree.top)
                        start.linkTo(degree.end)
                    }
                    .padding(top = 10.dp),
                style = TextStyle(fontSize = 30.sp)
            )
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(cardDetail) {
                        top.linkTo(degree.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    .height(80.dp)
                    .padding(horizontal = 30.dp)
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)  // Optional padding inside the ConstraintLayout
                ) {
                    val (humidityTitle, humidityValue, uvTitle, uvValue, feelLikeTitle, feelLikeValue) = createRefs()

                    Text(
                        modifier = Modifier
                            .constrainAs(humidityTitle) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                            .padding(start = 10.dp),
                        text = "Humidity",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        color = Color(
                            ContextCompat.getColor(
                                LocalContext.current,
                                R.color.color_c4c4c4
                            )
                        )
                    )
                    Text(
                        modifier = Modifier.constrainAs(humidityValue) {
                            top.linkTo(humidityTitle.bottom)
                            start.linkTo(humidityTitle.start)
                            end.linkTo(humidityTitle.end)
                            bottom.linkTo(parent.bottom)
                        },
                        text = String.format("%s%%", dataWeather?.current?.humidity.toString()),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        color = Color(
                            ContextCompat.getColor(
                                LocalContext.current,
                                R.color.color_9a9a9a
                            )
                        ),
                    )

                    Text(
                        modifier = Modifier.constrainAs(uvTitle) {
                            top.linkTo(parent.top)
                            start.linkTo(humidityTitle.end)
                            end.linkTo(feelLikeTitle.start)
                            centerHorizontallyTo(parent)  // Optional for horizontal centering
                        },
                        text = "UV",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        color = Color(
                            ContextCompat.getColor(
                                LocalContext.current,
                                R.color.color_c4c4c4
                            )
                        )
                    )
                    Text(
                        text = dataWeather?.current?.uv.toString(),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        color = Color(
                            ContextCompat.getColor(
                                LocalContext.current,
                                R.color.color_9a9a9a
                            )
                        ),
                        modifier = Modifier.constrainAs(uvValue) {
                            top.linkTo(uvTitle.bottom)
                            start.linkTo(uvTitle.start)
                            end.linkTo(uvTitle.end)
                            bottom.linkTo(parent.bottom)
                        }
                    )

                    Text(
                        modifier = Modifier
                            .constrainAs(feelLikeTitle) {
                                top.linkTo(uvTitle.top)
                                end.linkTo(parent.end)
                                bottom.linkTo(uvTitle.bottom)
                            }
                            .padding(end = 10.dp),
                        text = "Feels Like",
                        fontSize = 10.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        color = Color(
                            ContextCompat.getColor(
                                LocalContext.current,
                                R.color.color_c4c4c4
                            )
                        ),
                    )
                    Text(
                        text = String.format("%s°", dataWeather?.current?.feelslike_c.toString()),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        color = Color(
                            ContextCompat.getColor(
                                LocalContext.current,
                                R.color.color_9a9a9a
                            )
                        ),
                        modifier = Modifier.constrainAs(feelLikeValue) {
                            top.linkTo(feelLikeTitle.bottom)
                            end.linkTo(feelLikeTitle.end)
                            start.linkTo(feelLikeTitle.start)
                            bottom.linkTo(parent.bottom)
                        }
                    )
                }
            }

        }
    }

    private fun Modifier.visible(visible: Boolean): Modifier {
        return if (visible) this else this.then(Modifier.alpha(0f))
    }

    @Composable
    fun HideKeyboard() {
        val keyboardController = LocalSoftwareKeyboardController.current
        Button(onClick = {
            keyboardController?.hide()
        }) {
            Text("Hide Keyboard")
        }
    }
}


