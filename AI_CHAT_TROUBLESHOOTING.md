# 🔧 AI Chat Troubleshooting Guide

## ❌ **"Gemini API is not working"**

---

## 🔍 **Common Issues & Solutions:**

### **Issue 1: API Key Invalid or Expired**

**Symptoms:**

- Error message in chat
- "API key invalid or expired"
- HTTP Error 403

**Solution:**

```
1. Get a NEW Gemini API key:
   → https://makersuite.google.com/app/apikey
   
2. Open: GeminiService.kt
   
3. Line 19: Replace the old key with your new one:
   private const val API_KEY = "YOUR_NEW_KEY_HERE"
   
4. Make sure:
   ✓ Key starts with "AIza"
   ✓ About 39 characters long
   ✓ No extra spaces
   ✓ Inside quotes ""
   
5. Build → Rebuild Project
   
6. Run the app again
```

---

### **Issue 2: No Internet Connection**

**Symptoms:**

- Long loading time
- Timeout errors
- "Connection failed"

**Solution:**

```
1. Check device/emulator has internet
2. Try opening a website in browser
3. Restart emulator if needed
4. Check firewall settings
```

---

### **Issue 3: Seeing Old "Download Model" Screen**

**Symptoms:**

- "Downloading: 3%"
- "Available Models" section
- "Qwen 2.5 0.5B Instruct Q6_K"

**Solution:**

```
This means the app needs to be rebuilt!

1. Build → Clean Project
2. Build → Rebuild Project
3. Wait for completion
4. Run ▶️

The new AI Chat will now appear!
```

---

## ✅ **Current Setup:**

### **AI Chat Now Uses 3 Methods (in order):**

1. **Gemini API** (Primary)
    - Google's FREE AI
    - Requires API key
    - Needs internet
    - Best quality

2. **RunAnywhere AI** (Fallback)
    - Local, offline AI
    - No API key needed
    - Works without internet
    - Good quality

3. **Error Message** (Last Resort)
    - Helpful troubleshooting info
    - Link to get API key
    - Status information

---

## 🧪 **Testing Your Setup:**

### **Test 1: Check API Key**

```
1. Open: GeminiService.kt
2. Line 19: Check the API_KEY value
3. Should be: "AIzaXXXXXXXXXXXXXXXXXXXXX..."
4. NOT: "YOUR_GEMINI_API_KEY_HERE"

✅ If you have a real key = Good!
❌ If still placeholder = Need to add key
```

### **Test 2: Check Logcat**

```
1. Run the app
2. Open AI Chat
3. Type a message
4. In Android Studio → Logcat
5. Filter by: "GeminiService" or "ChatScreen"

Look for:
✅ "Response code: 200" = Working!
✅ "Successfully generated content" = Working!
❌ "HTTP Error 403" = Invalid API key
❌ "API key invalid" = Need new key
❌ "ERROR: API error" = Check internet
```

### **Test 3: Try Sending Message**

```
1. Open AI Chat
2. Type: "Hello"
3. Click Send
4. Wait 5-10 seconds

✅ If you get a response = Working!
❌ If you get error message = See solutions below
```

---

## 🔑 **How to Get Gemini API Key (FREE):**

### **Step-by-Step:**

```
1. Visit: https://makersuite.google.com/app/apikey
   (or: https://aistudio.google.com/app/apikey)

2. Sign in with Google account

3. Click "Create API Key"

4. Select "Create API key in new project"

5. Copy the key (starts with "AIza...")

6. Open: app/src/main/java/.../api/GeminiService.kt

7. Line 19: Paste your key:
   private const val API_KEY = "AIzaXXXXXXXXX..."

8. Save the file

9. Build → Rebuild Project

10. Run the app

11. Test AI Chat!
```

**Cost:** $0.00 (FREE!)
**Time:** 2 minutes
**No credit card** needed!

---

## 🆘 **Error Messages & What They Mean:**

### **"API key invalid or expired"**

```
Problem: Your API key doesn't work
Solution: Get a new key from https://makersuite.google.com/app/apikey
```

### **"Too many requests"**

```
Problem: You've used your free quota
Solution: Wait 1 minute, or get another API key
```

### **"Connection failed" / "Timeout"**

```
Problem: No internet or firewall blocking
Solution: Check internet, restart emulator, check firewall
```

### **"I'm having trouble connecting..."**

```
Problem: Both Gemini and RunAnywhere failed
Solution: Check API key, check internet, restart app
```

---

## 🔧 **Advanced Troubleshooting:**

### **Check APIConfig:**

```kotlin
// File: APIConfig.kt line 23
val CURRENT_PROVIDER = AIProvider.GEMINI  // Should be GEMINI

✅ If GEMINI = Correct
❌ If WIKIPEDIA = Change to GEMINI
❌ If OPENAI = Change to GEMINI (unless you have OpenAI key)
```

### **Verify GeminiService:**

```kotlin
// File: GeminiService.kt line 19
private const val API_KEY = "AIzaXXXXXXXX..."

✅ Starts with "AIza"
✅ About 39 characters
✅ In quotes ""
✅ No spaces

❌ "YOUR_GEMINI_API_KEY_HERE" = Need to replace!
```

### **Check Internet Permissions:**

```xml
// File: AndroidManifest.xml
<uses-permission android:name="android.permission.INTERNET" />

✅ If present = Good
❌ If missing = Add it
```

---

## 🎯 **Expected Behavior:**

### **When Working:**

```
1. Type message
2. Click send
3. See typing indicator (3 dots)
4. Wait 3-5 seconds
5. Get AI response!
6. Can ask follow-up questions
7. AI remembers conversation
```

### **First Time:**

```
1. Opens with welcome message
2. Shows 5 quick prompt buttons
3. Type or click prompt
4. Get instant response
5. Prompts disappear
6. Chat continues normally
```

---

## 📊 **Current Status Check:**

### **Run This Checklist:**

```
□ API key is set (not placeholder)
□ API key starts with "AIza"
□ API key is 39 characters
□ App has been rebuilt
□ Device has internet
□ APIConfig.kt uses GEMINI provider
□ No build errors
□ Chat screen loads
```

**If ALL checked = Should work!**
**If ANY unchecked = That's the problem!**

---

## 💡 **Alternative: Use Wikipedia Mode**

If Gemini keeps failing, you can use Wikipedia as fallback:

```kotlin
// File: APIConfig.kt line 23
val CURRENT_PROVIDER = AIProvider.WIKIPEDIA

Then:
1. Rebuild project
2. AI Chat will use Wikipedia
3. Study Mode still works great!
```

---

## 🆘 **Still Not Working?**

### **Last Resort Steps:**

1. **Clean Everything:**

```
Build → Clean Project
Build → Rebuild Project
Run ▶️
```

2. **Check Logcat Output:**

```
Filter: "ChatScreen" or "GeminiService"
Look for red ERROR lines
Share the error message
```

3. **Try Simple Test:**

```
1. Open Study Mode (not Chat)
2. Search "Python"
3. If this works → Gemini API is fine
4. If this fails → API key is the problem
```

4. **Verify App Permissions:**

```
Settings → Apps → VibeMentor → Permissions
Make sure "Internet" is allowed
```

---

## ✅ **Summary:**

### **Most Common Issue:**

**API Key not configured** → Get from https://makersuite.google.com/app/apikey

### **Second Most Common:**

**App not rebuilt** → Build → Rebuild Project

### **Third Most Common:**

**No internet** → Check connection

---

## 🎯 **Quick Fix (Try This First):**

```
1. Get API key: https://makersuite.google.com/app/apikey
2. Open: GeminiService.kt
3. Line 19: Paste your key
4. Build → Rebuild Project
5. Run ▶️
6. Test AI Chat
```

**This fixes 90% of issues!** ✨

---

**Need more help?** Check Logcat for specific error messages!
