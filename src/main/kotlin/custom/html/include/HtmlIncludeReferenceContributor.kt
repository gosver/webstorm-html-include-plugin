package custom.html.include

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext

class HtmlIncludeReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(XmlAttributeValue::class.java),
            IncludeSrcReferenceProvider()
        )
    }
}

class IncludeSrcReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<PsiReference> {

        val attributeValue = element as? XmlAttributeValue
            ?: return PsiReference.EMPTY_ARRAY

        val attribute = attributeValue.parent as? XmlAttribute
            ?: return PsiReference.EMPTY_ARRAY

        if (attribute.name != "src")
            return PsiReference.EMPTY_ARRAY

        val tag = attribute.parent as? XmlTag
            ?: return PsiReference.EMPTY_ARRAY

        if (tag.name.lowercase() != "include")
            return PsiReference.EMPTY_ARRAY

        val pathText = attributeValue.value.trim()

        if (pathText.isBlank())
            return PsiReference.EMPTY_ARRAY

        return object : FileReferenceSet(
            pathText,
            attributeValue,
            1,
            null,
            true,
            false,
            null
        ) {

            override fun getDefaultContexts(): Collection<PsiFileSystemItem> {
                val psiFile = attributeValue.containingFile
                    ?: return super.getDefaultContexts()

                val project = psiFile.project

                val baseDir = project.baseDir
                    ?: return super.getDefaultContexts()

                val srcDir = baseDir.findChild("src") ?: baseDir

                val psiSrcDir = PsiManager
                    .getInstance(project)
                    .findDirectory(srcDir)

                return if (psiSrcDir != null) {
                    listOf(psiSrcDir)
                } else {
                    super.getDefaultContexts()
                }
            }
        }.allReferences.map { it as PsiReference }.toTypedArray()
    }
}