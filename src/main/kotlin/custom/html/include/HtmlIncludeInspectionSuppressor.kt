package custom.html.include

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag

class HtmlIncludeInspectionSuppressor : InspectionSuppressor {

    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (toolId == "HtmlUnknownTag") {
            val tag = element as? XmlTag ?: element.parent as? XmlTag
            if (tag?.name?.equals("include", ignoreCase = true) == true) {
                return true
            }
        }
        return false
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> {
        return SuppressQuickFix.EMPTY_ARRAY
    }
}