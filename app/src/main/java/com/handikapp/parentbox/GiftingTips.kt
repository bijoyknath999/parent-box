package com.handikapp.parentbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.handikapp.parentbox.RecyclerView.GiftingAdapter
import com.handikapp.parentbox.Utils.Constants

class GiftingTips : AppCompatActivity() {
    var GiftingText : String = ""
    private lateinit var listView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gifting_tips)


        listView = findViewById(R.id.gifting_tips_list_view)
// 1
        val arrayList = arrayOf("Buy gifts that are safe and appropriate for the child's age, ability, and level of physical and brain development.",
                "Don’t gift children in such a way that you satisfy a child’s gratification instantly. Rather make the child wait for the gift and delay gratification and promote patience.",
                "The cost of the gift or toy is not what matters. You can enhance the value of the reward by giving it with appreciation and respect.",
                "Avoid toys that have sharp edges, tiny parts, or moving pieces, especially for very young children.",
                "Avoid gifts that are poorly made or flimsy or gifts that include ropes or cords.",
                "Every child has self-esteem. When you gift  them, do it with respect and don’t forget to add some words of appreciation.",
                "Don’t forget any safety equipment that needs to be included in the gift. For example, if you're gifting a skateboard or a bike, don’t forget  to include helmet as part of the package.",
                "Small but frequent gifts are more effective than bigger rewards served rarely. For example, giving a candy every time the child behaves as expected is more effective than saving them for an ice cream at the end of the month.",
                "Avoid toys that command children to do some manual tasks, which they just need to follow. For example, toys which direct children by asking them to press buttons or perform tasks. Such toys do little to encourage creative play.",
                "Choose toys that provide children with a platform for play rather than asking them what to play. Go for toys that allow children to use their imagination.  Examples are social toys like tea sets or kitchen sets. If you buy other toys like teddy bears or human figures encourage them to play with it socially.",
                "Gifting should be done in such a way that it doesn’t function as a bribe. For example, if your child is grossly misbehaving in a department store and you instantly reach for the shelf and give him a candy and tell “Take this and be quiet”, it can be considered a bribe. On the other hand, the next day when you come back from work and upon reaching home you give him the candy and appreciate how well he behaved the previous day, it’s a reward.",
                "Toys that can be taken apart and reconstructed into other shapes or objects encourage children to use their imagination and will provide them with hours of play. Play Dough, pens, crayons and sketchbooks are examples. Dolls, teddy bears and action men encourage children to create their own stories through role play.",
                "Rewards need not be something material.  Praise, words of encouragement, hugs and pats are examples of non-material rewards. Parents should try to substitute material rewards with non-material rewards in the long run. But bribes are always something material, like chocolates or money.",
                "Go for toys which encourage social interaction and allow more than one child to play at a time, like many board games. Let them interact with other children and play socially.",
                "Children love to play socially and parents need to encourage it. When promoting social games, parents should try to avoid games which generate happiness by hurting a fellow being, like shooting and war games. Instead, games which help to develop positive emotional feelings in the child, like medical sets, can be selected. These are social games which help children identify others’ sufferings, empathize with them, help them and finally receive satisfaction from the whole experience.",
                "Beware of toys that are marketed with extra ordinary taglines on the box. If the claim on the box is promising to make your child smarter, accelerate brain development or make the child bilingual - beware. Such claims are extravagant and often unfounded.")


//
        val myListAdapter = GiftingAdapter(this, arrayList)
        listView.adapter = myListAdapter


    }
}