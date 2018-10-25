package boxresin.android.kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/** 테스트용 두 번째 액티비티 */
class SecondActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setResult(RESULT_OK, Intent().apply {
			putExtra("message", "두 번째 액티비티입니다.")
		})
		finish()
	}
}
