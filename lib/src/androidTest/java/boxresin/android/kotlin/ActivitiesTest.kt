package boxresin.android.kotlin

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.test.rule.ActivityTestRule
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ActivitiesTest
{
	@get:Rule
	val activityRule = ActivityTestRule(TestActivity::class.java)

	private val activity by lazy { activityRule.activity as FragmentActivity }

	/** `startActivity` 기본 테스트 */
	@Test
	fun startActivityBasic() = runBlocking {
		val result = async(Dispatchers.Main) {
			activity.startActivityForResult(Intent(activity, SecondActivity::class.java))
		}.await()

		assertEquals(Activity.RESULT_OK, result.resultCode)
		assertEquals("두 번째 액티비티입니다.", result.data!!.getStringExtra("message"))
	}

	/** 동시에 여러 액티비티 실행 테스트 */
	@Test
	fun startActivities() = runBlocking {
		val first = async(Dispatchers.Main) {
			activity.startActivityForResult(Intent(activity, ThirdActivity::class.java))
		}
		val second = async(Dispatchers.Main) {
			activity.startActivityForResult(Intent(activity, SecondActivity::class.java))
		}

		with(second.await()) {
			assertEquals("두 번째 액티비티입니다.", data!!.getStringExtra("message"))
		}

		with(first.await()) {
			assertEquals("3 초간 대기한 뒤 종료합니다.", data!!.getStringExtra("alert"))
		}
	}

	/** [FragmentActivity]에서 실행한 액티비티와 [Fragment]에서 실행한 액티비티를 잘 구분하는지 테스트 */
	@Test
	fun startActivityConfuse() = runBlocking {
		// 프래그먼트에서 실행
		val execution = async(Dispatchers.Main) {
			activity.startActivityForResult(Intent(activity, ThirdActivity::class.java))
		}

		// 동일한 요청 코드로 액티비티에서 실행
		launch(Dispatchers.Main) {
			activity.startActivityForResult(Intent(activity, SecondActivity::class.java), 0xF0F0)
		}

		with(execution.await()) {
			assertEquals("3 초간 대기한 뒤 종료합니다.", data!!.getStringExtra("alert"))
		}
	}
}
