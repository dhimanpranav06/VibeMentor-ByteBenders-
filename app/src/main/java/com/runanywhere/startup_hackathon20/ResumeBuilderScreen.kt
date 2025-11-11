package com.runanywhere.startup_hackathon20

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.runanywhere.startup_hackathon20.api.NvidiaService

data class ResumeData(
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var summary: String = "",
    var education: String = "",
    var experience: String = "",
    var skills: String = "",
    var projects: String = "",
    var certifications: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(navController: NavController) {
    val gradientColors = listOf(
        Color(0xFF000428), // Deep navy
        Color(0xFF004E92)  // Bright blue
    )

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(12000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = ""
    )

    val particleAnim = rememberInfiniteTransition(label = "")
    val particleShift by particleAnim.animateFloat(
        initialValue = 0f, targetValue = 800f,
        animationSpec = infiniteRepeatable(
            tween(7000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = ""
    )

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var resumeData by remember { mutableStateOf(ResumeData()) }
    var showPreview by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var aiGenerating by remember { mutableStateOf("") } // Track which section is generating

    // AI Resume Content Generator using NvidiaService
    suspend fun generateAIContent(section: String, context: String): String {
        var result = ""
        try {
            val prompt = when (section) {
                "summary" -> """Write a professional 3-4 line summary for a resume. 
Context: ${if (context.isNotBlank()) context else "professional with diverse skills"}
Role: ${resumeData.name.ifBlank { "Professional" }}

Make it impactful and achievement-focused. Just return the summary text."""

                "experience" -> """Generate a professional work experience entry. 
Role/Company: ${if (context.isNotBlank()) context else "Software Developer at Tech Company"}
Format:
[Job Title] | [Company Name]
[Duration]
• [Achievement/Responsibility 1]
• [Achievement/Responsibility 2]
• [Achievement/Responsibility 3]

Use action verbs and quantify achievements where possible."""

                "skills" -> """List 8-12 relevant technical and soft skills for someone in:
Field: ${if (context.isNotBlank()) context else "technology and professional development"}

Format: Skill1, Skill2, Skill3, ...
Mix technical and soft skills."""

                "projects" -> """Generate a project description.
Project idea: ${if (context.isNotBlank()) context else "a technical project"}

Format:
[Project Name]
• Brief description and impact
• Technologies: [List key technologies]
• Outcome: [Results achieved]"""

                "education" -> """Write an education entry.
Degree/School: ${if (context.isNotBlank()) context else "Bachelor's Degree in Computer Science"}

Format:
[Degree Name]
[University Name], [Year/Duration]
• Key achievements or relevant coursework"""

                else -> "Generate content for: $section with context: $context"
            }

            result = NvidiaService.generateCompletion(prompt)
        } catch (e: Exception) {
            result = "Error generating content. Please enter manually."
        }
        return result.ifBlank { "Please try again or enter content manually." }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = androidx.compose.ui.geometry.Offset(gradientShift, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, gradientShift)
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // 🌟 Floating glowing particles
        for (i in 1..25) {
            val randomX = remember { (0..1000).random().toFloat() }
            val randomY = remember { (0..1800).random().toFloat() }
            Canvas(
                modifier = Modifier
                    .size((6..10).random().dp)
                    .offset(
                        x = (randomX + particleShift / 10).dp,
                        y = (randomY - particleShift / 20).dp
                    )
                    .blur((6..12).random().dp)
            ) {
                drawCircle(Color.White.copy(alpha = 0.15f), style = Stroke(2f))
            }
        }

        // 🧩 Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🏁 Header Section
            Text(
                text = "💼 AI Resume Builder",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your professional resume in minutes!",
                color = Color(0xFFB3E5FC),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Personal Information Section
            ResumeSection(title = "👤 Personal Information", icon = "👤") {
                ResumeTextField(
                    value = resumeData.name,
                    onValueChange = { resumeData = resumeData.copy(name = it) },
                    label = "Full Name",
                    placeholder = "John Doe"
                )
                ResumeTextField(
                    value = resumeData.email,
                    onValueChange = { resumeData = resumeData.copy(email = it) },
                    label = "Email Address",
                    placeholder = "john.doe@email.com"
                )
                ResumeTextField(
                    value = resumeData.phone,
                    onValueChange = { resumeData = resumeData.copy(phone = it) },
                    label = "Phone Number",
                    placeholder = "+1 (555) 123-4567"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Professional Summary with AI
            ResumeSection(title = "📝 Professional Summary", icon = "📝") {
                ResumeTextField(
                    value = resumeData.summary,
                    onValueChange = { resumeData = resumeData.copy(summary = it) },
                    label = "Summary",
                    placeholder = "Brief professional summary highlighting your key strengths...",
                    minLines = 3
                )
                AIGenerateButton(
                    isGenerating = aiGenerating == "summary",
                    onClick = {
                        coroutineScope.launch {
                            aiGenerating = "summary"
                            val generated = generateAIContent("summary", resumeData.name)
                            resumeData = resumeData.copy(summary = generated)
                            aiGenerating = ""
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Education with AI
            ResumeSection(title = "🎓 Education", icon = "🎓") {
                ResumeTextField(
                    value = resumeData.education,
                    onValueChange = { resumeData = resumeData.copy(education = it) },
                    label = "Education Details",
                    placeholder = "Bachelor of Science in Computer Science\nUniversity Name, 2020-2024",
                    minLines = 3
                )
                AIGenerateButton(
                    isGenerating = aiGenerating == "education",
                    onClick = {
                        coroutineScope.launch {
                            aiGenerating = "education"
                            val generated = generateAIContent("education", resumeData.education)
                            resumeData = resumeData.copy(education = generated)
                            aiGenerating = ""
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Experience with AI
            ResumeSection(title = "💼 Work Experience", icon = "💼") {
                ResumeTextField(
                    value = resumeData.experience,
                    onValueChange = { resumeData = resumeData.copy(experience = it) },
                    label = "Experience",
                    placeholder = "Software Engineer | Company Name\nJan 2023 - Present\n• Achievement 1\n• Achievement 2",
                    minLines = 4
                )
                AIGenerateButton(
                    isGenerating = aiGenerating == "experience",
                    onClick = {
                        coroutineScope.launch {
                            aiGenerating = "experience"
                            val generated = generateAIContent("experience", resumeData.experience)
                            resumeData = resumeData.copy(experience = generated)
                            aiGenerating = ""
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skills with AI
            ResumeSection(title = "💡 Skills", icon = "💡") {
                ResumeTextField(
                    value = resumeData.skills,
                    onValueChange = { resumeData = resumeData.copy(skills = it) },
                    label = "Skills",
                    placeholder = "Python, JavaScript, React, Node.js, Machine Learning, etc.",
                    minLines = 2
                )
                AIGenerateButton(
                    isGenerating = aiGenerating == "skills",
                    onClick = {
                        coroutineScope.launch {
                            aiGenerating = "skills"
                            val generated = generateAIContent("skills", resumeData.skills)
                            resumeData = resumeData.copy(skills = generated)
                            aiGenerating = ""
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Projects with AI
            ResumeSection(title = "🚀 Projects", icon = "🚀") {
                ResumeTextField(
                    value = resumeData.projects,
                    onValueChange = { resumeData = resumeData.copy(projects = it) },
                    label = "Projects",
                    placeholder = "Project Name 1\n• Description\n• Technologies used\n\nProject Name 2\n• Description",
                    minLines = 4
                )
                AIGenerateButton(
                    isGenerating = aiGenerating == "projects",
                    onClick = {
                        coroutineScope.launch {
                            aiGenerating = "projects"
                            val generated = generateAIContent("projects", resumeData.projects)
                            resumeData = resumeData.copy(projects = generated)
                            aiGenerating = ""
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Certifications (no AI, typically straightforward)
            ResumeSection(title = "🏅 Certifications", icon = "🏅") {
                ResumeTextField(
                    value = resumeData.certifications,
                    onValueChange = { resumeData = resumeData.copy(certifications = it) },
                    label = "Certifications",
                    placeholder = "AWS Certified Developer\nGoogle Cloud Professional\netc.",
                    minLines = 2
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Preview Button
                Button(
                    onClick = { showPreview = !showPreview },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E0FF)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (showPreview) "✏️" else "👁️",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showPreview) "Edit" else "Preview",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Generate PDF Button
                val shimmerAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.7f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        tween(1500, easing = LinearEasing),
                        RepeatMode.Reverse
                    ),
                    label = ""
                )

                Button(
                    onClick = {
                        if (resumeData.name.isNotBlank()) {
                            isGenerating = true
                            generateResumePDF(context, resumeData)
                            Toast.makeText(
                                context,
                                "Resume generated successfully! ✓",
                                Toast.LENGTH_LONG
                            ).show()
                            isGenerating = false
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter at least your name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .scale(if (isGenerating) 0.95f else 1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = shimmerAlpha)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF001F54)
                        )
                    } else {
                        Text(
                            text = "📥",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Export",
                            color = Color(0xFF001F54),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preview Section
            AnimatedVisibility(visible = showPreview) {
                ResumePreviewCard(resumeData)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back Button
            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    text = " Back to Career Mode",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ResumeSection(
    title: String,
    icon: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp,
                    color = Color(0xFF00E0FF),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

// Reusable TextField Component
@Composable
fun ResumeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFFB3E5FC), fontSize = 14.sp) },
        placeholder = { Text(placeholder, color = Color.White.copy(0.4f), fontSize = 14.sp) },
        textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 15.sp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            focusedIndicatorColor = Color(0xFFB3E5FC),
            unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f),
            cursorColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        minLines = minLines
    )
}

@Composable
fun AIGenerateButton(isGenerating: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00E0FF)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isGenerating) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        } else {
            Text(
                text = "Generate with AI",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ResumePreviewCard(data: ResumeData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = " Resume Preview",
                color = Color(0xFFFFD700),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (data.name.isNotBlank()) {
                Text(
                    text = data.name,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (data.email.isNotBlank() || data.phone.isNotBlank()) {
                Text(
                    text = buildString {
                        if (data.email.isNotBlank()) append(" ${data.email}")
                        if (data.email.isNotBlank() && data.phone.isNotBlank()) append(" | ")
                        if (data.phone.isNotBlank()) append(" ${data.phone}")
                    },
                    color = Color.White.copy(0.9f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (data.summary.isNotBlank()) {
                PreviewSection("Professional Summary", data.summary)
            }
            if (data.education.isNotBlank()) {
                PreviewSection("Education", data.education)
            }
            if (data.experience.isNotBlank()) {
                PreviewSection("Experience", data.experience)
            }
            if (data.skills.isNotBlank()) {
                PreviewSection("Skills", data.skills)
            }
            if (data.projects.isNotBlank()) {
                PreviewSection("Projects", data.projects)
            }
            if (data.certifications.isNotBlank()) {
                PreviewSection("Certifications", data.certifications)
            }
        }
    }
}

@Composable
fun PreviewSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title.uppercase(),
            color = Color(0xFF00E0FF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = Color.White.copy(0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            color = Color.White.copy(0.9f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// PDF Generation Function (Basic Text File for now - can be enhanced with actual PDF library)
fun generateResumePDF(context: Context, data: ResumeData) {
    try {
        val fileName = "${data.name.replace(" ", "_")}_Resume.txt"
        val file = File(context.getExternalFilesDir(null), fileName)

        FileOutputStream(file).use { output ->
            val resumeText = buildString {
                appendLine("=".repeat(50))
                appendLine(data.name.uppercase())
                appendLine("=".repeat(50))
                appendLine()
                if (data.email.isNotBlank()) appendLine("Email: ${data.email}")
                if (data.phone.isNotBlank()) appendLine("Phone: ${data.phone}")
                appendLine()

                if (data.summary.isNotBlank()) {
                    appendLine("PROFESSIONAL SUMMARY")
                    appendLine("-".repeat(50))
                    appendLine(data.summary)
                    appendLine()
                }

                if (data.education.isNotBlank()) {
                    appendLine("EDUCATION")
                    appendLine("-".repeat(50))
                    appendLine(data.education)
                    appendLine()
                }

                if (data.experience.isNotBlank()) {
                    appendLine("WORK EXPERIENCE")
                    appendLine("-".repeat(50))
                    appendLine(data.experience)
                    appendLine()
                }

                if (data.skills.isNotBlank()) {
                    appendLine("SKILLS")
                    appendLine("-".repeat(50))
                    appendLine(data.skills)
                    appendLine()
                }

                if (data.projects.isNotBlank()) {
                    appendLine("PROJECTS")
                    appendLine("-".repeat(50))
                    appendLine(data.projects)
                    appendLine()
                }

                if (data.certifications.isNotBlank()) {
                    appendLine("CERTIFICATIONS")
                    appendLine("-".repeat(50))
                    appendLine(data.certifications)
                    appendLine()
                }

                appendLine("=".repeat(50))
                appendLine("Generated by VibeMentor - AI Resume Builder")
            }

            output.write(resumeText.toByteArray())
        }

        // TODO: In production, use iText7 or similar library for actual PDF generation
        // For now, saves as text file in app directory

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
