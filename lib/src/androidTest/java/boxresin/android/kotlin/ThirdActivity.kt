package boxresin.android.kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/** 테스트용 세 번째 액티비티 */
class ThirdActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setResult(RESULT_OK, Intent().apply {
			putExtra("alert", "3 초간 대기한 뒤 종료합니다.")
		})

		GlobalScope.launch(Dispatchers.Main) {
			delay(3000)
			finish()
		}
	}
}
