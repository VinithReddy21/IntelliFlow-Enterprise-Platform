import io
import fitz  # PyMuPDF
import hashlib
import logging
import zipfile
import xml.etree.ElementTree as ET
from typing import Tuple

logger = logging.getLogger("intelliflow.services.text_extraction")

class TextExtractionService:
    """
    Enterprise Text Extraction Service supporting PDF, DOCX, TXT, and Markdown files.
    """

    def extract_text(self, file_bytes: bytes, filename: str, mime_type: str) -> Tuple[str, str]:
        """
        Extracts raw text content and calculates SHA-256 checksum for uploaded files.
        """
        checksum = hashlib.sha256(file_bytes).hexdigest()
        filename_lower = filename.lower()

        if filename_lower.endswith(".pdf") or "pdf" in mime_type:
            text = self._extract_from_pdf(file_bytes)
        elif filename_lower.endswith(".docx") or "word" in mime_type:
            text = self._extract_from_docx(file_bytes)
        else:
            # Default TXT and Markdown text decoding
            try:
                text = file_bytes.decode("utf-8")
            except UnicodeDecodeError:
                text = file_bytes.decode("latin-1", errors="ignore")

        logger.info(f"Extracted {len(text)} characters from {filename} (Checksum: {checksum[:8]}...)")
        return text, checksum

    def _extract_from_pdf(self, file_bytes: bytes) -> str:
        extracted = []
        try:
            doc = fitz.open(stream=file_bytes, filetype="pdf")
            for page in doc:
                text = page.get_text()
                if text:
                    extracted.append(text)
            doc.close()
        except Exception as e:
            logger.error(f"PyMuPDF PDF extraction failed: {str(e)}")
        return "\n\n".join(extracted)

    def _extract_from_docx(self, file_bytes: bytes) -> str:
        paragraphs = []
        try:
            with zipfile.ZipFile(io.BytesIO(file_bytes)) as z:
                xml_content = z.read("word/document.xml")
                tree = ET.fromstring(xml_content)
                # WordprocessingML namespace
                ns = {"w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}
                for p in tree.iter(f"{{{ns['w']}}}p"):
                    texts = [t.text for t in p.iter(f"{{{ns['w']}}}t") if t.text]
                    if texts:
                        paragraphs.append("".join(texts))
        except Exception as e:
            logger.error(f"DOCX XML text extraction failed: {str(e)}")
        return "\n\n".join(paragraphs)

text_extraction_service = TextExtractionService()
