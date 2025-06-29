package net.sonmoosans.jdak.event

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.sonmoosans.jdak.command.NullableTypedCommandOption
import net.sonmoosans.jdak.command.OptionsContainer
import net.sonmoosans.jdak.command.SerializableCommandOption
import net.sonmoosans.jdak.command.TypedCommandOption

class SlashCommandContext(
    val event: SlashCommandInteractionEvent,
) {
    val options = hashMapOf<String, Any?>()

    fun parseOptions(command: OptionsContainer) {
        for (option in command.options) {
            this.options[option.name] = option.parse(event)
        }
    }

    inline operator fun <reified T> NullableTypedCommandOption<T>.invoke() = value()
    inline operator fun <reified T> TypedCommandOption<T>.invoke() = value()

    inline fun <reified T> NullableTypedCommandOption<T>.value(): T? {
        if (!this.required && !options.containsKey(name) || options[name] == null) {
            return null
        }

        if (this.type == OptionType.STRING) {
            return Json.decodeFromString<T>(options[name] as String)
        }

        return options[name] as T
    }

    inline fun <reified T> TypedCommandOption<T>.value(): T {
        if (this is SerializableCommandOption<T> && this.type == OptionType.STRING) {
            val optionValue = options[name] as String

            val quotedValue = if (optionValue.startsWith("\"") && optionValue.endsWith("\"")) {
                optionValue
            } else {
                "\"$optionValue\""
            }


            return Json.decodeFromString(this.serializer as DeserializationStrategy<T>, quotedValue) as T
        }

        return options[name] as T
    }
}
