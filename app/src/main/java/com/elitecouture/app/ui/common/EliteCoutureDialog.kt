package com.elitecouture.app.ui.common

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.elitecouture.app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Clase personalizada para crear diálogos consistentes con la identidad visual de Elite Couture.
 * 
 * Características:
 * - Tipografía Elite Sans Semibold
 * - Botones con mayúsculas y subrayado
 * - Colores de marca consistentes
 * - Accesibilidad mejorada
 */
class EliteCoutureDialog private constructor(
    private val context: Context,
    private val builder: MaterialAlertDialogBuilder
) {
    
    companion object {
        /**
         * Crea un nuevo constructor de diálogo.
         */
        fun create(context: Context): Builder {
            return Builder(context)
        }
    }
    
    /**
     * Builder pattern para construir diálogos personalizados.
     */
    class Builder(private val context: Context) {
        private var title: String? = null
        private var message: String? = null
        private var positiveButtonText: String? = null
        private var positiveButtonAction: (() -> Unit)? = null
        private var positiveButtonColor: Int? = null
        private var negativeButtonText: String? = null
        private var negativeButtonAction: (() -> Unit)? = null
        private var negativeButtonColor: Int? = null
        private var neutralButtonText: String? = null
        private var neutralButtonAction: (() -> Unit)? = null
        private var neutralButtonColor: Int? = null
        private var cancelable: Boolean = true
        
        /**
         * Establece el título del diálogo.
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }
        
        /**
         * Establece el título del diálogo desde recursos.
         */
        fun setTitle(titleResId: Int): Builder {
            this.title = context.getString(titleResId)
            return this
        }
        
        /**
         * Establece el mensaje del diálogo.
         */
        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }
        
        /**
         * Establece el mensaje del diálogo desde recursos.
         */
        fun setMessage(messageResId: Int): Builder {
            this.message = context.getString(messageResId)
            return this
        }
        
        /**
         * Establece el botón positivo (ej: "GUARDAR", "SALIR").
         */
        fun setPositiveButton(text: String, action: (() -> Unit)? = null): Builder {
            this.positiveButtonText = text
            this.positiveButtonAction = action
            return this
        }
        
        /**
         * Establece el botón positivo desde recursos.
         */
        fun setPositiveButton(textResId: Int, action: (() -> Unit)? = null): Builder {
            this.positiveButtonText = context.getString(textResId)
            this.positiveButtonAction = action
            return this
        }
        
        /**
         * Establece el color del botón positivo.
         */
        fun setPositiveButtonColor(colorResId: Int): Builder {
            this.positiveButtonColor = context.getColor(colorResId)
            return this
        }
        
        /**
         * Establece el botón negativo (ej: "CANCELAR").
         */
        fun setNegativeButton(text: String, action: (() -> Unit)? = null): Builder {
            this.negativeButtonText = text
            this.negativeButtonAction = action
            return this
        }
        
        /**
         * Establece el botón negativo desde recursos.
         */
        fun setNegativeButton(textResId: Int, action: (() -> Unit)? = null): Builder {
            this.negativeButtonText = context.getString(textResId)
            this.negativeButtonAction = action
            return this
        }
        
        /**
         * Establece el color del botón negativo.
         */
        fun setNegativeButtonColor(colorResId: Int): Builder {
            this.negativeButtonColor = context.getColor(colorResId)
            return this
        }
        
        /**
         * Establece el botón neutral (ej: "DESCARTAR").
         */
        fun setNeutralButton(text: String, action: (() -> Unit)? = null): Builder {
            this.neutralButtonText = text
            this.neutralButtonAction = action
            return this
        }
        
        /**
         * Establece el botón neutral desde recursos.
         */
        fun setNeutralButton(textResId: Int, action: (() -> Unit)? = null): Builder {
            this.neutralButtonText = context.getString(textResId)
            this.neutralButtonAction = action
            return this
        }
        
        /**
         * Establece el color del botón neutral.
         */
        fun setNeutralButtonColor(colorResId: Int): Builder {
            this.neutralButtonColor = context.getColor(colorResId)
            return this
        }
        
        /**
         * Establece si el diálogo se puede cancelar tocando fuera o con el botón Back.
         */
        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }
        
        /**
         * Construye y muestra el diálogo.
         */
        fun show() {
            val builder = MaterialAlertDialogBuilder(context, R.style.EliteCoutureDialogTheme)
            
            // Configurar título
            title?.let { builder.setTitle(it) }
            
            // Configurar mensaje
            message?.let { builder.setMessage(it) }
            
            // Configurar botón positivo
            positiveButtonText?.let { text ->
                builder.setPositiveButton(text) { dialog, _ ->
                    positiveButtonAction?.invoke()
                    dialog.dismiss()
                }
            }
            
            // Configurar botón negativo
            negativeButtonText?.let { text ->
                builder.setNegativeButton(text) { dialog, _ ->
                    negativeButtonAction?.invoke()
                    dialog.dismiss()
                }
            }
            
            // Configurar botón neutral
            neutralButtonText?.let { text ->
                builder.setNeutralButton(text) { dialog, _ ->
                    neutralButtonAction?.invoke()
                    dialog.dismiss()
                }
            }
            
            // Configurar cancelable
            builder.setCancelable(cancelable)
            
            // Crear y mostrar el diálogo
            val dialog = builder.create()
            dialog.show()
            
            // Aplicar estilos personalizados a los botones después de mostrar
            applyButtonStyles(dialog)
        }
        
        /**
         * Aplica estilos personalizados a los botones del diálogo.
         */
        private fun applyButtonStyles(dialog: androidx.appcompat.app.AlertDialog) {
            val font = ResourcesCompat.getFont(context, R.font.elite_sans_semibold)
            val defaultColor = context.getColor(R.color.color_primary)
            
            // Estilizar botón positivo
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(positiveButtonColor ?: defaultColor)
                typeface = font
                textSize = 12f
                letterSpacing = 0.05f
                paintFlags = paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                isAllCaps = true
                contentDescription = positiveButtonText
            }
            
            // Estilizar botón negativo
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(negativeButtonColor ?: defaultColor)
                typeface = font
                textSize = 12f
                letterSpacing = 0.05f
                paintFlags = paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                isAllCaps = true
                contentDescription = negativeButtonText
            }
            
            // Estilizar botón neutral
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL)?.apply {
                setTextColor(neutralButtonColor ?: defaultColor)
                typeface = font
                textSize = 12f
                letterSpacing = 0.05f
                paintFlags = paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                isAllCaps = true
                contentDescription = neutralButtonText
            }
        }
    }
}
