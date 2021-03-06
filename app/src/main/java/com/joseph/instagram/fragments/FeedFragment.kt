package com.joseph.instagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.joseph.instagram.MainActivity
import com.joseph.instagram.Post
import com.joseph.instagram.PostAdapter
import com.joseph.instagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

open class FeedFragment : Fragment() {

    lateinit var postsRecyclerView: RecyclerView

    lateinit var adapter: PostAdapter

    var allPosts: MutableList<Post> = mutableListOf()

    lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up views and click listeners

       postsRecyclerView = view.findViewById(R.id.postRecyclerView)

        swipeContainer = view.findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing timeline")
            queryPosts()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        //Steps to populate RecyclerView
        // 1. Create layout for each row in list (item_post.xml)
        // 2. Create data source for each row (this is the Post class)
        // 3. Create adapter that will bridge data and row layout (PostAdapter)
        // 4. Set adapter on RecyclerView
        adapter = PostAdapter(requireContext(), allPosts)
        postsRecyclerView.adapter = adapter
        // 5. Set layout manager on RecyclerView
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        queryPosts()
    }

    // Query for all posts in our server
    open fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post objetcs
        query.include(Post.KEY_USER)
        //Return posts in descending order meaning new posts will appear first
        query.addDescendingOrder("createdAt")

        // TODO: Only return the most recent 20 posts

        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, p1: ParseException?) {
                if (p1 != null) {
                    // Something has went wrong
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if (posts != null) {
                        for (post in posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: " + post.getUser()?.username)
                        }

                        allPosts.clear()

                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                        // Now we call setRefreshing(false) to signal refresh has finished
                        swipeContainer.setRefreshing(false)
                    }
                }
            }
        })
    }

    fun clear() {
        allPosts.clear()
        adapter.notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(tweetList: List<Post>) {
        allPosts.addAll(tweetList)
        adapter.notifyDataSetChanged()
    }

    companion object {
        const val TAG = "FeedFragment"
    }
}