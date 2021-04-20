package com.scurab.kuproxy.util

object StringColors {
    const val ANSI_RESET = "\u001B[0m"

    fun white(value: String) = apply(value, AnsiEffect.TextWhite)
    fun yellow(value: String) = apply(value, AnsiEffect.TextYellow)
    fun gray(value: String) = apply(value, AnsiEffect.TextGray)
    fun apply(value: String, effect: AnsiEffect) = "\u001B[${String.format("0%d", effect.number)}m$value$ANSI_RESET"

    enum class AnsiEffect(val number: Int) {
        None(0),
        Bold(1),
        Italic(3),
        Underline(4),
        Invert(7),
        CrossOut(9),
        UnderlineBold(21),
        Frame(51),

        TextBlack(30),
        TextRed(31),
        TextGreen(32),
        TextYellow(33),
        TextBlue(34),
        TextMagenta(35),
        TextCyan(36),
        TextGray(37),
        TextDarkGray(90),
        TextRedBright(91),
        TextGreenBright(92),
        TextYellowBright(93),
        TextBlueBright(94),
        TextMagentaBright(95),
        TextCyanBright(96),
        TextWhite(97),
        BackgroundBlack(40),
        BackgroundRed(41),
        BackgroundGreen(42),
        BackgroundYellow(43),
        BackgroundBlue(44),
        BackgroundMagenta(45),
        BackgroundCyan(46),
        BackgroundGray(47),
        BackgroundBlackBright(100),
        BackgroundRedBright(101),
        BackgroundGreenBright(102),
        BackgroundYellowBright(103),
        BackgroundBlueBright(104),
        BackgroundMagentaBright(105),
        BackgroundCyanBright(106),
        BackgroundGrayBright(107),
    }
}
