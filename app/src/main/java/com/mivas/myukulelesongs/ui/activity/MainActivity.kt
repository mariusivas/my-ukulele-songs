package com.mivas.myukulelesongs.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.*
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import com.mivas.myukulelesongs.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.songsFragment,
                R.id.chordsFragment,
                R.id.virtualUkeFragment,
                R.id.settingsActivity
            ),
            drawerLayout
        )
        setupNavigation()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp(appBarConfiguration)

    private fun setupNavigation() {
        val navController = findNavController(R.id.navHostFragment)
        setupActionBarWithNavController(this, navController, appBarConfiguration)
        navigationView.itemIconTintList = null
        setupWithNavController(navigationView, navController)
    }

    override fun onOptionsItemSelected(item: MenuItem)= item.onNavDestinationSelected(findNavController(R.id.navHostFragment)) || super.onOptionsItemSelected(item)

}
