package com.example.fonts

import com.example.model.FontCategory
import com.example.model.FontStyle

object FontEngine {

    // Helper to map single char using offset ranges or lookup map
    private fun mapChars(text: String, upperBase: Int, lowerBase: Int, digitBase: Int? = null): String {
        val sb = StringBuilder()
        for (ch in text) {
            when {
                ch in 'A'..'Z' -> {
                    val codePoint = upperBase + (ch - 'A')
                    sb.append(String(Character.toChars(codePoint)))
                }
                ch in 'a'..'z' -> {
                    val codePoint = lowerBase + (ch - 'a')
                    sb.append(String(Character.toChars(codePoint)))
                }
                digitBase != null && ch in '0'..'9' -> {
                    val codePoint = digitBase + (ch - '0')
                    sb.append(String(Character.toChars(codePoint)))
                }
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun mapByLookup(text: String, map: Map<Char, String>): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(map[ch] ?: ch.toString())
        }
        return sb.toString()
    }

    // Special maps
    private val bubbleOutlinedMap = mapOf(
        'A' to "Ⓐ", 'B' to "Ⓑ", 'C' to "Ⓒ", 'D' to "Ⓓ", 'E' to "Ⓔ", 'F' to "Ⓕ", 'G' to "Ⓖ", 'H' to "Ⓗ", 'I' to "Ⓘ",
        'J' to "Ⓙ", 'K' to "Ⓚ", 'L' to "Ⓛ", 'M' to "Ⓜ", 'N' to "Ⓝ", 'O' to "Ⓞ", 'P' to "Ⓟ", 'Q' to "Ⓠ", 'R' to "Ⓡ",
        'S' to "Ⓢ", 'T' to "Ⓣ", 'U' to "Ⓤ", 'V' to "Ⓥ", 'W' to "Ⓦ", 'X' to "Ⓧ", 'Y' to "Ⓨ", 'Z' to "Ⓩ",
        'a' to "ⓐ", 'b' to "ⓑ", 'c' to "ⓒ", 'd' to "ⓓ", 'e' to "ⓔ", 'f' to "ⓕ", 'g' to "ⓖ", 'h' to "ⓗ", 'i' to "ⓘ",
        'j' to "ⓙ", 'k' to "ⓚ", 'l' to "ⓛ", 'm' to "ⓜ", 'n' to "ⓝ", 'o' to "ⓞ", 'p' to "ⓟ", 'q' to "ⓠ", 'r' to "ⓡ",
        's' to "ⓢ", 't' to "ⓣ", 'u' to "ⓤ", 'v' to "ⓥ", 'w' to "ⓦ", 'x' to "ⓧ", 'y' to "ⓨ", 'z' to "ⓩ",
        '0' to "⓪", '1' to "①", '2' to "②", '3' to "③", '4' to "④", '5' to "⑤", '6' to "⑥", '7' to "⑦", '8' to "⑧", '9' to "⑨"
    )

    private val bubbleFilledMap = mapOf(
        'A' to "🅐", 'B' to "🅑", 'C' to "🅒", 'D' to "🅓", 'E' to "🅔", 'F' to "🅕", 'G' to "🅖", 'H' to "🅢", 'I' to "🅘",
        'J' to "🅵", 'K' to "🅚", 'L' to "🅛", 'M' to "🅜", 'N' to "🅝", 'O' to "🅞", 'P' to "🅟", 'Q' to "🅠", 'R' to "🅡",
        'S' to "🅢", 'T' to "🅣", 'U' to "🅤", 'V' to "🅥", 'W' to "🅯", 'X' to "🅍", 'Y' to "🅨", 'Z' to "🅩",
        'a' to "🅐", 'b' to "🅑", 'c' to "🅒", 'd' to "🅓", 'e' to "🅔", 'f' to "🅕", 'g' to "🅖", 'h' to "🅢", 'i' to "🅘",
        'j' to "🅵", 'k' to "🅚", 'l' to "🅛", 'm' to "🅜", 'n' to "🅝", 'o' to "🅞", 'p' to "🅟", 'q' to "🅠", 'r' to "🅡",
        's' to "🅢", 't' to "🅣", 'u' to "🅤", 'v' to "🅥", 'w' to "🅯", 'x' to "🅍", 'y' to "🅨", 'z' to "🅩",
        '0' to "⓿", '1' to "❶", '2' to "❷", '3' to "❸", '4' to "❹", '5' to "❺", '6' to "❻", '7' to "❼", '8' to "❽", '9' to "❾"
    )

    private val squareOutlinedMap = mapOf(
        'A' to "🄰", 'B' to "🄱", 'C' to "🄲", 'D' to "🄳", 'E' to "🄴", 'F' to "🄵", 'G' to "🄷", 'H' to "🄷", 'I' to "🄸",
        'J' to "🄹", 'K' to "🄺", 'L' to "🄻", 'M' to "🄼", 'N' to "🄽", 'O' to "🄾", 'P' to "🄿", 'Q' to "🅀", 'R' to "🅁",
        'S' to "🅂", 'T' to "🅃", 'U' to "🅄", 'V' to "🅅", 'W' to "7", 'X' to "🅈", 'Y' to "🅉", 'Z' to "🅉",
        'a' to "🄰", 'b' to "🄱", 'c' to "🄲", 'd' to "🄳", 'e' to "🄴", 'f' to "🄵", 'g' to "🄷", 'h' to "🄷", 'i' to "🄸",
        'j' to "🄹", 'k' to "🄺", 'l' to "🄻", 'm' to "🄼", 'n' to "🄽", 'o' to "🄾", 'p' to "🄿", 'q' to "🅀", 'r' to "🅁",
        's' to "🅂", 't' to "🅃", 'u' to "🅄", 'v' to "🅅", 'w' to "7", 'x' to "🅈", 'y' to "🅉", 'z' to "🅉",
        '0' to "🄌", '1' to "🄁", '2' to "🄂", '3' to "🄃", '4' to "🄄", '5' to "🄅", '6' to "🄆", '7' to "🄇", '8' to "🄈", '9' to "🄉"
    )

    private val squareFilledMap = mapOf(
        'A' to "🅰", 'B' to "🅱", 'C' to "🅒", 'D' to "🅳", 'E' to "🅴", 'F' to "🅵", 'G' to "🅖", 'H' to "🅷", 'I' to "🅸",
        'J' to "🅹", 'K' to "🅺", 'L' to "🅻", 'M' to "🅼", 'N' to "🅽", 'O' to "🅾", 'P' to "🅿", 'Q' to "🅬", 'R' to "🆁",
        'S' to "🅢", 'T' to "🅃", 'U' to "🅄", 'V' to "🅅", 'W' to "7", 'X' to "🅈", 'Y' to "🅉", 'Z' to "🅉",
        'a' to "🅰", 'b' to "🅱", 'c' to "🅒", 'd' to "🅳", 'e' to "🅴", 'f' to "🅵", 'g' to "🅖", 'h' to "🅷", 'i' to "🅸",
        'j' to "🅹", 'k' to "🅺", 'l' to "🅻", 'm' to "🅼", 'n' to "🅽", 'o' to "🅾", 'p' to "🅿", 'q' to "🅬", 'r' to "🆁",
        's' to "🅢", 't' to "🅃", 'u' to "🅄", 'v' to "🅅", 'w' to "7", 'x' to "🅈", 'y' to "🅉", 'z' to "🅉",
        '0' to "0", '1' to "1", '2' to "2", '3' to "3", '4' to "4", '5' to "5", '6' to "6", '7' to "7", '8' to "8", '9' to "9"
    )

    private val smallCapsMap = mapOf(
        'a' to "ᴀ", 'b' to "ʙ", 'c' to "ᴄ", 'd' to "ᴅ", 'e' to "ᴇ", 'f' to "ꜰ", 'g' to "ɢ", 'h' to "ʜ", 'i' to "ɪ",
        'j' to "ᴊ", 'k' to "ᴋ", 'l' to "ʟ", 'm' to "ᴍ", 'n' to "ɴ", 'o' to "ᴏ", 'p' to "ᴘ", 'q' to "ǫ", 'r' to "ʀ",
        's' to "ꜱ", 't' to "ᴛ", 'u' to "ᴜ", 'v' to "ᴠ", 'w' to "ᴡ", 'x' to "x", 'y' to "ʏ", 'z' to "ᴢ",
        'A' to "ᴀ", 'B' to "ʙ", 'C' to "ᴄ", 'D' to "ᴅ", 'E' to "ᴇ", 'F' to "ꜰ", 'G' to "ɢ", 'H' to "ʜ", 'I' to "ɪ",
        'J' to "ᴊ", 'K' to "ᴋ", 'L' to "ʟ", 'M' to "ᴍ", 'N' to "ɴ", 'O' to "ᴏ", 'P' to "ᴘ", 'Q' to "ǫ", 'R' to "ʀ",
        'S' to "ꜱ", 'T' to "ᴛ", 'U' to "ᴜ", 'V' to "ᴠ", 'W' to "ᴡ", 'X' to "x", 'Y' to "ʏ", 'Z' to "ᴢ"
    )

    private val superscriptMap = mapOf(
        'a' to "ᵃ", 'b' to "ᵇ", 'c' to "ᶜ", 'd' to "ᵈ", 'e' to "ᵉ", 'f' to "ᶠ", 'g' to "ᵍ", 'h' to "ʰ", 'i' to "ⁱ",
        'j' to "ʲ", 'k' to "ᵏ", 'l' to "ˡ", 'm' to "ᵐ", 'n' to "ⁿ", 'o' to "ᵒ", 'p' to "ᵖ", 'r' to "ʳ", 's' to "ˢ",
        't' to "ᵗ", 'u' to "ᵘ", 'v' to "ᵛ", 'w' to "ʷ", 'x' to "ˣ", 'y' to "ʸ", 'z' to "ᶻ",
        'A' to "ᴬ", 'B' to "ᴮ", 'D' to "ᴰ", 'E' to "ᴱ", 'G' to "ᴎ", 'H' to "ᴴ", 'I' to "ᴵ", 'J' to "ᴶ", 'K' to "ᴷ",
        'L' to "ᴸ", 'M' to "ᴹ", 'N' to "ᴺ", 'O' to "ᴼ", 'P' to "ᴾ", 'R' to "ᴿ", 'T' to "ᵀ", 'U' to "ᵁ", 'V' to "ⱽ",
        'W' to "ᵂ", '0' to "⁰", '1' to "¹", '2' to "²", '3' to "³", '4' to "⁴", '5' to "⁵", '6' to "⁶", '7' to "⁷", '8' to "⁸", '9' to "⁹"
    )

    private val subscriptMap = mapOf(
        'a' to "ₐ", 'e' to "ₑ", 'h' to "ₕ", 'i' to "ᵢ", 'j' to "ⱼ", 'k' to "ₖ", 'l' to "ₗ", 'm' to "ₘ", 'n' to "ₙ",
        'o' to "ₒ", 'p' to "ₚ", 'r' to "ᵣ", 's' to "ₛ", 't' to "ₜ", 'u' to "ᵤ", 'v' to "ᵥ", 'x' to "ₓ",
        '0' to "₀", '1' to "₁", '2' to "₂", '3' to "₃", '4' to "₄", '5' to "₅", '6' to "₆", '7' to "₇", '8' to "₈", '9' to "₉"
    )

    private val upsideDownMap = mapOf(
        'a' to "ɐ", 'b' to "q", 'c' to "ɔ", 'd' to "p", 'e' to "ǝ", 'f' to "ɟ", 'g' to "ɓ", 'h' to "ɥ", 'i' to "ı",
        'j' to "ɾ", 'k' to "ʞ", 'l' to "l", 'm' to "ɯ", 'n' to "u", 'o' to "o", 'p' to "d", 'q' to "b", 'r' to "ɹ",
        's' to "s", 't' to "ʇ", 'u' to "n", 'v' to "ʌ", 'w' to "ʍ", 'x' to "x", 'y' to "ʎ", 'z' to "z",
        'A' to "∀", 'B' to "𐐒", 'C' to "Ɔ", 'D' to "◖", 'E' to "Ǝ", 'F' to "Ⅎ", 'G' to "⅁", 'H' to "H", 'I' to "I",
        'J' to "ſ", 'K' to "⋊", 'L' to "⅂", 'M' to "W", 'N' to "N", 'O' to "O", 'P' to "Ԁ", 'Q' to "𐌞", 'R' to "ᴚ",
        'S' to "S", 'T' to "┴", 'U' to "∩", 'V' to "Λ", 'W' to "M", 'X' to "X", 'Y' to "⅄", 'Z' to "Z",
        '0' to "0", '1' to "Ɩ", '2' to "乙", '3' to "Ɛ", '4' to "ㄣ", '5' to "ϛ", '6' to "9", '7' to "L", '8' to "8", '9' to "6"
    )

    private fun combineWithChar(text: String, markChar: Char): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(ch).append(markChar)
        }
        return sb.toString()
    }

    val ALL_FONTS: List<FontStyle> = listOf(
        // BOLD
        FontStyle(
            id = "bold_sans",
            name = "Bold Sans",
            category = FontCategory.BOLD,
            sampleText = "𝗔𝗕𝗖 𝟭𝟮𝟯",
            transform = { text -> mapChars(text, 0x1D5A0, 0x1D5BA, 0x1D7EC) }
        ),
        FontStyle(
            id = "bold_serif",
            name = "Bold Serif",
            category = FontCategory.BOLD,
            sampleText = "𝐀𝐁𝐂 𝟏𝟐𝟑",
            transform = { text -> mapChars(text, 0x1D400, 0x1D41A, 0x1D7CE) }
        ),

        // ITALIC
        FontStyle(
            id = "italic_sans",
            name = "Italic Sans",
            category = FontCategory.ITALIC,
            sampleText = "𝘈𝘉𝘊 𝟣𝟤𝟥",
            transform = { text -> mapChars(text, 0x1D608, 0x1D622, 0x1D7E2) }
        ),
        FontStyle(
            id = "italic_serif",
            name = "Italic Serif",
            category = FontCategory.ITALIC,
            sampleText = "𝘈𝘉𝘊 𝘹𝘺𝘻",
            transform = { text -> mapChars(text, 0x1D434, 0x1D44E) }
        ),
        FontStyle(
            id = "bold_italic_sans",
            name = "Bold Italic Sans",
            category = FontCategory.ITALIC,
            sampleText = "𝘽𝙤𝙡𝙙 𝙄𝙩𝙖𝙡𝙞𝙘",
            transform = { text -> mapChars(text, 0x1D63C, 0x1D656) }
        ),
        FontStyle(
            id = "bold_italic_serif",
            name = "Bold Italic Serif",
            category = FontCategory.ITALIC,
            sampleText = "𝑩𝒐𝒍𝒅 𝑰𝒕𝒂𝒍𝒊𝒄",
            transform = { text -> mapChars(text, 0x1D468, 0x1D482) }
        ),

        // SANS
        FontStyle(
            id = "sans_serif",
            name = "Sans Serif",
            category = FontCategory.SANS,
            sampleText = "Ѕаnѕ Ѕtylе",
            transform = { text -> mapChars(text, 0x1D56C, 0x1D586) }
        ),

        // MONOSPACE
        FontStyle(
            id = "monospace",
            name = "Monospace",
            category = FontCategory.MONOSPACE,
            sampleText = "𝙼𝚘𝚗𝚘𝚜𝚙𝚊𝚌𝚎 𝟶𝟷𝟸",
            transform = { text -> mapChars(text, 0x1D670, 0x1D68A, 0x1D7F6) }
        ),

        // DOUBLE STRIKE / BLACKBOARD
        FontStyle(
            id = "double_strike",
            name = "Double Strike",
            category = FontCategory.DOUBLE_STRIKE,
            sampleText = "𝔻𝕠𝕦𝕓𝕝𝕖 𝕊𝕥𝕣𝕚𝕜𝕖 𝟘𝟙𝟚",
            transform = { text -> mapChars(text, 0x1D538, 0x1D552, 0x1D7D8) }
        ),

        // BUBBLE
        FontStyle(
            id = "bubble_outlined",
            name = "Bubble Outlined",
            category = FontCategory.BUBBLE,
            sampleText = "ⓐⓑⓒ ①②③",
            transform = { text -> mapByLookup(text, bubbleOutlinedMap) }
        ),
        FontStyle(
            id = "bubble_filled",
            name = "Bubble Filled",
            category = FontCategory.BUBBLE,
            sampleText = "🅐🅑🅒 ❶❷❸",
            transform = { text -> mapByLookup(text, bubbleFilledMap) }
        ),

        // SQUARE
        FontStyle(
            id = "square_outlined",
            name = "Square Outlined",
            category = FontCategory.SQUARE,
            sampleText = "🄰🄱🄲 🄁🄂🄃",
            transform = { text -> mapByLookup(text, squareOutlinedMap) }
        ),
        FontStyle(
            id = "square_filled",
            name = "Square Filled",
            category = FontCategory.SQUARE,
            sampleText = "🅰🅱🅒 123",
            transform = { text -> mapByLookup(text, squareFilledMap) }
        ),

        // TINY
        FontStyle(
            id = "small_caps",
            name = "Small Caps",
            category = FontCategory.TINY,
            sampleText = "ꜱᴍᴀʟʟ ᴄᴀᴘꜱ",
            transform = { text -> mapByLookup(text, smallCapsMap) }
        ),
        FontStyle(
            id = "superscript",
            name = "Superscript Tiny",
            category = FontCategory.TINY,
            sampleText = "ᵃᵇᶜ ⁰¹²³",
            transform = { text -> mapByLookup(text, superscriptMap) }
        ),
        FontStyle(
            id = "subscript",
            name = "Subscript Tiny",
            category = FontCategory.TINY,
            sampleText = "ₐᵦ𝒸 ₀₁₂₃",
            transform = { text -> mapByLookup(text, subscriptMap) }
        ),

        // CLASSIC / SCRIPT / FRAKTUR
        FontStyle(
            id = "script_cursive",
            name = "Cursive Script",
            category = FontCategory.CLASSIC,
            sampleText = "𝓈𝒸𝓇𝒾𝓅𝓉 𝓉ℯ𝓍𝓉",
            transform = { text -> mapChars(text, 0x1D49C, 0x1D4B6) }
        ),
        FontStyle(
            id = "bold_script",
            name = "Bold Script",
            category = FontCategory.CLASSIC,
            sampleText = "𝓼𝓬𝓻𝓲𝓹𝓽 𝓫𝓸𝓵𝓭",
            transform = { text -> mapChars(text, 0x1D4D0, 0x1D4EA) }
        ),
        FontStyle(
            id = "fraktur_gothic",
            name = "Fraktur Gothic",
            category = FontCategory.CLASSIC,
            sampleText = "𝔉𝔯𝔞𝔨𝔱𝔲𝔯",
            transform = { text -> mapChars(text, 0x1D504, 0x1D51E) }
        ),
        FontStyle(
            id = "bold_fraktur",
            name = "Bold Fraktur",
            category = FontCategory.CLASSIC,
            sampleText = "𝕱𝔯𝔞𝔨𝔱𝔲𝔯",
            transform = { text -> mapChars(text, 0x1D56C, 0x1D586) }
        ),

        // FANCY
        FontStyle(
            id = "fullwidth",
            name = "Fullwidth Wide",
            category = FontCategory.FANCY,
            sampleText = "Ｆｕｌｌｗｉｄｔｈ",
            transform = { text ->
                val sb = StringBuilder()
                for (ch in text) {
                    when (ch) {
                        ' ' -> sb.append("  ")
                        in '!'..'~' -> sb.append((ch.code + 0xFEE0).toChar())
                        else -> sb.append(ch)
                    }
                }
                sb.toString()
            }
        ),
        FontStyle(
            id = "strikethrough",
            name = "Strikethrough",
            category = FontCategory.FANCY,
            sampleText = "S̶t̶r̶i̶k̶e̶",
            transform = { text -> combineWithChar(text, '\u0336') }
        ),
        FontStyle(
            id = "underline_double",
            name = "Double Underline",
            category = FontCategory.FANCY,
            sampleText = "U̳n̳d̳e̳r̳l̳i̳n̳e̳",
            transform = { text -> combineWithChar(text, '\u0333') }
        ),
        FontStyle(
            id = "slashed",
            name = "Slash Strike",
            category = FontCategory.FANCY,
            sampleText = "S̸l̸a̸s̸h̸",
            transform = { text -> combineWithChar(text, '\u0338') }
        ),
        FontStyle(
            id = "upside_down",
            name = "Upside Down",
            category = FontCategory.FANCY,
            sampleText = "pʍou ǝpısd∩",
            transform = { text ->
                val sb = StringBuilder()
                val reversed = text.reversed()
                for (ch in reversed) {
                    sb.append(upsideDownMap[ch] ?: ch.toString())
                }
                sb.toString()
            }
        ),

        // SYMBOLS & FRAMES
        FontStyle(
            id = "fancy_wings",
            name = "Fancy Wings",
            category = FontCategory.SYMBOLS,
            sampleText = "꧁༺ FontNova ༻꧂",
            transform = { text -> "꧁༺ $text ༻꧂" }
        ),
        FontStyle(
            id = "star_borders",
            name = "Star Borders",
            category = FontCategory.SYMBOLS,
            sampleText = "★彡 FontNova 彡★",
            transform = { text -> "★彡 $text 彡★" }
        ),
        FontStyle(
            id = "sparkle_frame",
            name = "Sparkle Frame",
            category = FontCategory.SYMBOLS,
            sampleText = "✨ FontNova ✨",
            transform = { text -> "✨ $text ✨" }
        ),
        FontStyle(
            id = "heart_frame",
            name = "Heart Frame",
            category = FontCategory.SYMBOLS,
            sampleText = "♥ FontNova ♥",
            transform = { text -> "♥ $text ♥" }
        ),
        FontStyle(
            id = "bracket_frame",
            name = "Bracket Frame",
            category = FontCategory.SYMBOLS,
            sampleText = "【 FontNova 】",
            transform = { text -> "【 $text 】" }
        ),
        FontStyle(
            id = "crown_wings",
            name = "Crown Wings",
            category = FontCategory.SYMBOLS,
            sampleText = "👑 FontNova 👑",
            transform = { text -> "👑 $text 👑" }
        ),

        // NUMBERS SPECIFIC
        FontStyle(
            id = "numbers_bold_sans",
            name = "Bold Sans Numbers",
            category = FontCategory.NUMBERS,
            sampleText = "𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵",
            transform = { text -> mapChars(text, 0, 0, 0x1D7EC) }
        ),
        FontStyle(
            id = "numbers_sans_light",
            name = "Light Sans Numbers",
            category = FontCategory.NUMBERS,
            sampleText = "𝟢𝟣𝟤𝟥𝟦𝟧𝟨𝟩𝟪𝟫",
            transform = { text -> mapChars(text, 0, 0, 0x1D7E2) }
        ),
        FontStyle(
            id = "numbers_double_struck",
            name = "Blackboard Numbers",
            category = FontCategory.NUMBERS,
            sampleText = "𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡",
            transform = { text -> mapChars(text, 0, 0, 0x1D7D8) }
        ),
        FontStyle(
            id = "numbers_circled_outlined",
            name = "Circled Outlined",
            category = FontCategory.NUMBERS,
            sampleText = "⓪①②③④⑤⑥⑦⑧⑨",
            transform = { text -> mapByLookup(text, bubbleOutlinedMap) }
        ),
        FontStyle(
            id = "numbers_circled_filled",
            name = "Circled Filled",
            category = FontCategory.NUMBERS,
            sampleText = "⓿❶❷❸❹❺❻❼❽❾",
            transform = { text -> mapByLookup(text, bubbleFilledMap) }
        )
    )

    fun getFontsByCategory(category: FontCategory): List<FontStyle> {
        if (category == FontCategory.ALL) return ALL_FONTS
        return ALL_FONTS.filter { it.category == category }
    }

    fun searchFonts(query: String): List<FontStyle> {
        if (query.isBlank()) return ALL_FONTS
        return ALL_FONTS.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.category.displayName.contains(query, ignoreCase = true) ||
            it.sampleText.contains(query, ignoreCase = true)
        }
    }

    fun transformText(text: String, fontId: String): String {
        val font = ALL_FONTS.firstOrNull { it.id == fontId } ?: return text
        return font.transform(text)
    }
}
