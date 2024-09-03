import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import com.ite.sws.R

/**
 * 툴바 설정 함수
 *
 * @author 정은지
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02   정은지       최초 생성
 * </pre>
 *
 * @param toolbar 툴바 객체
 * @param title 툴바에 표시할 타이틀
 * @param showBackButton 뒤로가기 버튼 표시 여부
 */

fun Fragment.setupToolbar(toolbar: Toolbar, title: String, showBackButton: Boolean = false) {
    (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
    (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)

    toolbar.findViewById<TextView>(R.id.toolbar_title)?.text = title

    if (showBackButton) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    } else {
        toolbar.navigationIcon = null
    }
}
