package com.effort.feature.community

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.effort.feature.community.giveaway.GiveAwayFragment
import com.effort.feature.community.meetup.MeetupFragment
import com.effort.feature.community.question.QuestionFragment

class CommunityDetailAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> QuestionFragment()
            1 -> GiveAwayFragment()
            2 -> MeetupFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}