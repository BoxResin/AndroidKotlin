package boxresin.android.kotlin

import android.content.Intent
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * 액티비티를 실행하고 종료될 때까지 대기한다.
 * @return 액티비티 실행 결과
 */
@UiThread
suspend fun FragmentActivity.startActivityForResult(
		intent: Intent): ActivityResult = suspendCoroutine { continuation ->

	// 액티비티에 콜백 수신용 프래그먼트를 부착한다.
	val fragment = ActivityDelegateFragment()
	this.supportFragmentManager.beginTransaction()
			.add(fragment, null)
			.commitNow()

	// 코루틴을 재개하기 위한 Continuation 을 저장한다.
	ViewModelProviders.of(fragment)[Repository::class.java].continuation = continuation

	// 액티비티를 실행한다.
	fragment.startActivityForResult(intent, innerRequestCode)
}

/**
 * 액티비티 실행 결과
 * @property resultCode 결과 코드
 * @property data 결과 데이터
 */
data class ActivityResult(
		val resultCode: Int,
		val data: Intent?)

/** 내부적으로 사용하는 액티비티 요청 코드 */
private const val innerRequestCode = 0xF0F0

/** 액티비티 콜백 수신용 프래그먼트 */
internal class ActivityDelegateFragment : Fragment()
{
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		super.onActivityResult(requestCode, resultCode, data)

		if (requestCode == innerRequestCode)
		{
			// 코루틴을 재개한다.
			with(ViewModelProviders.of(this)[Repository::class.java]) {
				continuation?.resume(ActivityResult(resultCode, data))
				continuation = null
			}

			// 액티비티에서 콜백 수신용 프래그먼트를 제거한다.
			activity?.supportFragmentManager?.beginTransaction()
					?.remove(this)
					?.commit()
		}
	}
}

/** 프래그먼트로 데이터를 전달하기 위한 뷰 모델 */
internal class Repository : ViewModel()
{
	/** 코루틴을 재개하기 위한 Continuation */
	var continuation: Continuation<ActivityResult>? = null
}
