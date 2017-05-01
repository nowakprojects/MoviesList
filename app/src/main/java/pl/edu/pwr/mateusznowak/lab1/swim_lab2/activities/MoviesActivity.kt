package pl.edu.pwr.mateusznowak.lab1.swim_lab2.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_movies.*
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.R
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.adapters.MoviesRecyclerViewAdapter
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.helpers.PermissionsHelper
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.listeners.RecyclerTouchListener
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.models.Movie
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.retrofit.MoviesCalls
import android.support.v7.widget.helper.ItemTouchHelper.Callback.makeMovementFlags
import android.view.MotionEvent
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.dagger.DaggerMoviesComponent
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.dagger.MoviesComponent
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.dagger.MoviesModule
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.helpers.BaseItemTouchHelperCallback
import pl.edu.pwr.mateusznowak.lab1.swim_lab2.helpers.MoviesHelper
import javax.inject.Inject


class MoviesActivity : AppCompatActivity() {

    companion object {
        private val INTERNET_PERMISSION_CODE = 1
    }

    val moviesComponent:MoviesComponent = DaggerMoviesComponent.builder().moviesModule(MoviesModule()).build()

    @Inject
    lateinit var moviesHelper:MoviesHelper

    lateinit private var moviesAdapter:MoviesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        initDaggerDependencyInjection()
        showMoviesDataIfExists()

        initMoviesSwipeRefreshLayout()
        initMoviesRecyclerView()
    }

    private fun initDaggerDependencyInjection() {
        moviesComponent.inject(this)
        moviesAdapter = MoviesRecyclerViewAdapter(moviesHelper.moviesList)
    }

    private fun initMoviesRecyclerView() {
        rv_movies.layoutManager = LinearLayoutManager(this)
        rv_movies.adapter = moviesAdapter
        rv_movies.addOnItemTouchListener(RecyclerTouchListener(applicationContext,rv_movies,object: RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {
                /*val intent = Intent(applicationContext, javaClass)
                intent.putExtra(MovieDetailsActivity.MOVIE_TITLE_EXTRA,moviesList[position].title)
                startActivity(intent)*/
            }

            override fun onLongClick(view: View?, position: Int) {
                //moviesAdapter.toggleLongClickIcon(position);
            }

        }))

        ItemTouchHelper(BaseItemTouchHelperCallback(moviesAdapter)).attachToRecyclerView(rv_movies)

    }

    private fun initMoviesSwipeRefreshLayout() {
        srl_movies.setOnRefreshListener {
            srl_movies.isRefreshing=true
            showMoviesDataIfExists()
            srl_movies.isRefreshing=false
        }
    }

    fun showMoviesDataIfExists(){
        val moviesTitles = resources.getStringArray(R.array.starwars_movies)
        moviesHelper.downloadMoviesDataForTitles(moviesTitles)
        moviesAdapter.notifyDataSetChanged()
    }

    private fun showMoviesDownloadError() {
        Toast.makeText(baseContext,R.string.download_error,Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun askForInternetPermission(){
        PermissionsHelper.requestPermission(
                this,
                R.string.internet_permission_request,
                R.string.internet_permission_body,
                Array<String>(1){Manifest.permission.INTERNET},
                INTERNET_PERMISSION_CODE)
    }

}