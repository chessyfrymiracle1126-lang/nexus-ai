/**
 * NexusAI - Futuristic Neural Assistant (Web Edition)
 * Pure vanilla JavaScript - zero external dependencies
 */

(function () {
  'use strict';

  // State Management
  const state = {
    mode: 'CREATIVE', // CREATIVE, BALANCED, PRECISE
    prompt: '',
    rawResponse: '',
    isGenerating: false,
    isSpeaking: false,
    startTime: 0,
    provider: 'mock', // mock, gemini, custom
    apiKey: '',
    customEndpoint: 'https://api.openai.com/v1/chat/completions'
  };

  // Quick Prompt Chips Data
  const QUICK_PROMPTS = [
    {
      icon: '📝',
      label: 'Summarize',
      prompt: 'Summarize the core principles of quantum computing in 3 clear bullet points.'
    },
    {
      icon: '💡',
      label: 'Brainstorm Ideas',
      prompt: 'Brainstorm 5 innovative startup ideas combining artificial intelligence and clean renewable energy.'
    },
    {
      icon: '💻',
      label: 'Write Code',
      prompt: 'Write a clean, responsive CSS card component with glowing neon hover borders and glassmorphism.'
    },
    {
      icon: '🔬',
      label: 'Explain Simply',
      prompt: 'Explain how neural network transformers work as if I were a 12-year-old curious gamer.'
    },
    {
      icon: '✉️',
      label: 'Draft Email',
      prompt: 'Draft a polite and persuasive email requesting project feedback from a senior executive.'
    },
    {
      icon: '🐛',
      label: 'Debug Code',
      prompt: 'Debug this JavaScript async/await issue where race condition causes state mismatch.'
    }
  ];

  // DOM Elements
  const elements = {
    chipsContainer: document.getElementById('chipsContainer'),
    modePills: document.querySelectorAll('.mode-pill'),
    promptInput: document.getElementById('promptInput'),
    charCount: document.getElementById('charCount'),
    clearBtn: document.getElementById('clearBtn'),
    generateBtn: document.getElementById('generateBtn'),
    generateSpinner: document.getElementById('generateSpinner'),
    btnText: document.querySelector('.btn-text'),
    sendIcon: document.querySelector('.send-icon'),
    durationBadge: document.getElementById('durationBadge'),
    idleView: document.getElementById('idleView'),
    loadingView: document.getElementById('loadingView'),
    errorView: document.getElementById('errorView'),
    errorMessage: document.getElementById('errorMessage'),
    retryBtn: document.getElementById('retryBtn'),
    successView: document.getElementById('successView'),
    modeBadge: document.getElementById('modeBadge'),
    responseBody: document.getElementById('responseBody'),
    responseMetrics: document.getElementById('responseMetrics'),
    speakBtn: document.getElementById('speakBtn'),
    speakBtnText: document.getElementById('speakBtnText'),
    iconSpeak: document.querySelector('.icon-speak'),
    iconStop: document.querySelector('.icon-stop'),
    copyBtn: document.getElementById('copyBtn'),
    copyBtnText: document.getElementById('copyBtnText'),
    iconCopy: document.querySelector('.icon-copy'),
    iconCopied: document.querySelector('.icon-copied'),
    regenerateBtn: document.getElementById('regenerateBtn'),
    settingsBtn: document.getElementById('settingsBtn'),
    settingsModal: document.getElementById('settingsModal'),
    closeSettingsBtn: document.getElementById('closeSettingsBtn'),
    apiKeyGroup: document.getElementById('apiKeyGroup'),
    apiKeyInput: document.getElementById('apiKeyInput'),
    endpointGroup: document.getElementById('endpointGroup'),
    endpointInput: document.getElementById('endpointInput'),
    toggleApiKeyBtn: document.getElementById('toggleApiKeyBtn'),
    saveSettingsBtn: document.getElementById('saveSettingsBtn'),
    resetSettingsBtn: document.getElementById('resetSettingsBtn'),
    toast: document.getElementById('toast'),
    toastMessage: document.getElementById('toastMessage')
  };

  let toastTimer = null;
  let speechUtterance = null;

  // =========================================================================
  // Initialization
  // =========================================================================
  function init() {
    loadSettings();
    renderQuickPromptChips();
    setupEventListeners();
    updateCharCount();
  }

  // =========================================================================
  // Settings Management (Local Storage)
  // =========================================================================
  function loadSettings() {
    try {
      const savedProvider = localStorage.getItem('nexus_provider');
      const savedApiKey = localStorage.getItem('nexus_api_key');
      const savedEndpoint = localStorage.getItem('nexus_endpoint');

      if (savedProvider) state.provider = savedProvider;
      if (savedApiKey) state.apiKey = savedApiKey;
      if (savedEndpoint) state.customEndpoint = savedEndpoint;

      // Update Radio in Modal
      const radio = document.querySelector(`input[name="provider"][value="${state.provider}"]`);
      if (radio) radio.checked = true;

      elements.apiKeyInput.value = state.apiKey;
      elements.endpointInput.value = state.customEndpoint;

      updateProviderInputsVisibility();
    } catch (e) {
      console.warn('LocalStorage unavailable:', e);
    }
  }

  function saveSettings() {
    const selectedRadio = document.querySelector('input[name="provider"]:checked');
    if (selectedRadio) state.provider = selectedRadio.value;
    state.apiKey = elements.apiKeyInput.value.trim();
    state.customEndpoint = elements.endpointInput.value.trim();

    try {
      localStorage.setItem('nexus_provider', state.provider);
      localStorage.setItem('nexus_api_key', state.apiKey);
      localStorage.setItem('nexus_endpoint', state.customEndpoint);
    } catch (e) {
      console.warn('Unable to write to localStorage:', e);
    }

    closeSettingsModal();
    showToast('Engine settings successfully saved');
  }

  function resetSettings() {
    state.provider = 'mock';
    state.apiKey = '';
    state.customEndpoint = 'https://api.openai.com/v1/chat/completions';

    const mockRadio = document.querySelector('input[name="provider"][value="mock"]');
    if (mockRadio) mockRadio.checked = true;

    elements.apiKeyInput.value = '';
    elements.endpointInput.value = state.customEndpoint;
    updateProviderInputsVisibility();

    try {
      localStorage.removeItem('nexus_provider');
      localStorage.removeItem('nexus_api_key');
      localStorage.removeItem('nexus_endpoint');
    } catch (e) {}

    showToast('Settings reset to Built-in Neural Core');
  }

  function updateProviderInputsVisibility() {
    const selected = document.querySelector('input[name="provider"]:checked');
    const val = selected ? selected.value : 'mock';

    if (val === 'gemini') {
      elements.apiKeyGroup.classList.remove('hidden');
      elements.endpointGroup.classList.add('hidden');
    } else if (val === 'custom') {
      elements.apiKeyGroup.classList.remove('hidden');
      elements.endpointGroup.classList.remove('hidden');
    } else {
      elements.apiKeyGroup.classList.add('hidden');
      elements.endpointGroup.classList.add('hidden');
    }
  }

  // =========================================================================
  // Quick Prompt Chips
  // =========================================================================
  function renderQuickPromptChips() {
    elements.chipsContainer.innerHTML = '';
    QUICK_PROMPTS.forEach(item => {
      const chip = document.createElement('button');
      chip.className = 'quick-chip';
      chip.setAttribute('type', 'button');
      chip.innerHTML = `<span class="chip-icon">${item.icon}</span><span>${item.label}</span>`;
      chip.addEventListener('click', () => {
        elements.promptInput.value = item.prompt;
        state.prompt = item.prompt;
        updateCharCount();
        executeGeneration();
      });
      elements.chipsContainer.appendChild(chip);
    });
  }

  // =========================================================================
  // Event Listeners
  // =========================================================================
  function setupEventListeners() {
    // Mode Pills
    elements.modePills.forEach(pill => {
      pill.addEventListener('click', () => {
        elements.modePills.forEach(p => p.classList.remove('active'));
        pill.classList.add('active');
        state.mode = pill.getAttribute('data-mode');
        updateModeBadge(state.mode);
      });
    });

    // Prompt Textarea
    elements.promptInput.addEventListener('input', () => {
      state.prompt = elements.promptInput.value;
      updateCharCount();
    });

    elements.promptInput.addEventListener('keydown', (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault();
        executeGeneration();
      }
    });

    // Clear Button
    elements.clearBtn.addEventListener('click', () => {
      elements.promptInput.value = '';
      state.prompt = '';
      updateCharCount();
      elements.promptInput.focus();
    });

    // Generate Button
    elements.generateBtn.addEventListener('click', executeGeneration);

    // Retry Button
    elements.retryBtn.addEventListener('click', executeGeneration);

    // Regenerate Button
    elements.regenerateBtn.addEventListener('click', executeGeneration);

    // Copy Response Button
    elements.copyBtn.addEventListener('click', copyFullResponse);

    // Text-to-Speech Button
    elements.speakBtn.addEventListener('click', toggleSpeech);

    // Settings Modal
    elements.settingsBtn.addEventListener('click', openSettingsModal);
    elements.closeSettingsBtn.addEventListener('click', closeSettingsModal);
    elements.settingsModal.addEventListener('click', (e) => {
      if (e.target === elements.settingsModal) closeSettingsModal();
    });

    document.querySelectorAll('input[name="provider"]').forEach(r => {
      r.addEventListener('change', updateProviderInputsVisibility);
    });

    elements.toggleApiKeyBtn.addEventListener('click', () => {
      const isPassword = elements.apiKeyInput.type === 'password';
      elements.apiKeyInput.type = isPassword ? 'text' : 'password';
    });

    elements.saveSettingsBtn.addEventListener('click', saveSettings);
    elements.resetSettingsBtn.addEventListener('click', resetSettings);

    // Escape Key Listener
    window.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && !elements.settingsModal.classList.contains('hidden')) {
        closeSettingsModal();
      }
    });
  }

  // =========================================================================
  // Input Helpers
  // =========================================================================
  function updateCharCount() {
    const len = elements.promptInput.value.length;
    elements.charCount.textContent = `${len} / 2000`;
    if (len > 1800) {
      elements.charCount.classList.add('warning');
    } else {
      elements.charCount.classList.remove('warning');
    }

    if (len > 0) {
      elements.clearBtn.classList.remove('hidden');
    } else {
      elements.clearBtn.classList.add('hidden');
    }
  }

  function updateModeBadge(mode) {
    elements.modeBadge.className = 'mode-badge';
    if (mode === 'CREATIVE') {
      elements.modeBadge.textContent = 'Creative Mode';
      elements.modeBadge.classList.add('creative');
    } else if (mode === 'BALANCED') {
      elements.modeBadge.textContent = 'Balanced Mode';
      elements.modeBadge.classList.add('balanced');
    } else {
      elements.modeBadge.textContent = 'Precise Mode';
      elements.modeBadge.classList.add('precise');
    }
  }

  // =========================================================================
  // AI Generation Pipeline
  // =========================================================================
  async function executeGeneration() {
    const prompt = elements.promptInput.value.trim();
    if (!prompt) {
      elements.promptInput.focus();
      showToast('Please enter a prompt first');
      return;
    }

    // Stop active speech if speaking
    stopSpeech();

    // Set Loading State
    state.isGenerating = true;
    state.startTime = performance.now();
    elements.generateBtn.disabled = true;
    elements.btnText.textContent = 'Generating...';
    elements.sendIcon.classList.add('hidden');
    elements.generateSpinner.classList.remove('hidden');

    // Switch Views
    elements.idleView.classList.add('hidden');
    elements.errorView.classList.add('hidden');
    elements.successView.classList.add('hidden');
    elements.loadingView.classList.remove('hidden');
    elements.durationBadge.classList.add('hidden');

    try {
      let responseText = '';
      if (state.provider === 'gemini' && state.apiKey) {
        responseText = await callGeminiApi(prompt, state.mode, state.apiKey);
      } else if (state.provider === 'custom' && state.apiKey) {
        responseText = await callCustomEndpoint(prompt, state.mode, state.customEndpoint, state.apiKey);
      } else {
        responseText = await callMockNeuralEngine(prompt, state.mode);
      }

      const elapsed = ((performance.now() - state.startTime) / 1000).toFixed(1);
      state.rawResponse = responseText;

      // Render Result
      renderResponse(responseText, elapsed);
    } catch (err) {
      console.error('Generation failure:', err);
      showError(err.message || 'Failed to generate response. Please check settings.');
    } finally {
      state.isGenerating = false;
      elements.generateBtn.disabled = false;
      elements.btnText.textContent = 'Generate';
      elements.sendIcon.classList.remove('hidden');
      elements.generateSpinner.classList.add('hidden');
    }
  }

  // =========================================================================
  // Provider: Built-in Mock Neural Core
  // =========================================================================
  async function callMockNeuralEngine(prompt, mode) {
    // Artificial realistic delay (600ms - 1100ms)
    await new Promise(r => setTimeout(r, 700 + Math.random() * 400));

    const p = prompt.toLowerCase();

    // Contextual responses based on keywords
    if (p.includes('quantum computing') || p.includes('quantum')) {
      return `### Principles of Quantum Computing

Quantum computation harnesses non-classical physics to solve problems beyond polynomial classical limits:

- **Superposition**: Unlike classical bits constrained to discrete binary states (0 or 1), quantum qubits exist in linear combinations $\\alpha|0\\rangle + \\beta|1\\rangle$, enabling simultaneous exploration of immense search vectors.
- **Entanglement**: Qubits exhibit correlated wavefunctions across arbitrary spatial separations. Manipulating a single entrained qubit instantaneously reconfigures the global computational phase space.
- **Quantum Interference**: Quantum algorithms (such as Shor's and Grover's) intentionally induce constructive interference along optimal solution trajectories while destructively eliminating incorrect candidate paths.`;
    }

    if (p.includes('clean renewable energy') || p.includes('startup') || p.includes('energy')) {
      return `### 5 AI × Renewable Energy Startup Innovations

Here are high-impact venture concepts leveraging artificial intelligence for the clean energy transition:

1. **GridPulse AI**: Autonomous reinforcement learning system that balances regional hyper-local microgrids and battery storage dynamically against fluctuating solar/wind generation.
2. **HelioTwin Diagnostics**: Drone computer-vision mesh that maps thermal micro-cracks in multi-gigawatt photovoltaic solar fields with predictive maintenance schedules.
3. **GeoThermal Oracle**: Deep seismic transformer model that processes geophysical electromagnetic surveys to pinpoint subterranean hot dry rock reservoirs with 90% dry-well reduction.
4. **HydroFlow Predictor**: Multi-variable climate forecasting engine calculating snowpack runoff and turbine hydro-dispatch schedules 14 days in advance.
5. **BatteryDNA LifeCycle**: Physics-informed neural network evaluating degradation impedance curves of EV second-life lithium packs to repurpose them for grid storage.`;
    }

    if (p.includes('css') || p.includes('card') || p.includes('neon') || p.includes('component')) {
      return `### Futuristic Glowing Neon Glassmorphism Card

Here is a modern, responsive CSS component engineered with glassmorphism and cyan/purple hover borders:

\`\`\`css
.cyber-card {
  position: relative;
  background: rgba(14, 23, 47, 0.75);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(0, 242, 254, 0.25);
  border-radius: 16px;
  padding: 24px;
  color: #f8fafc;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.cyber-card::before {
  content: '';
  position: absolute;
  inset: -2px;
  background: linear-gradient(135deg, #00f2fe, #a855f7);
  border-radius: inherit;
  z-index: -1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.cyber-card:hover {
  transform: translateY(-4px);
  border-color: rgba(0, 242, 254, 0.8);
  box-shadow: 0 12px 32px rgba(0, 242, 254, 0.25),
              0 0 20px rgba(168, 85, 247, 0.2);
}

.cyber-card:hover::before {
  opacity: 0.15;
}
\`\`\`

**Key Features**:
- High-efficiency GPU-accelerated backdrop blur
- Smooth bezier curve hover elevation
- Neon gradient double-border glow effect`;
    }

    if (p.includes('transformer') || p.includes('gamer') || p.includes('12-year-old')) {
      return `### How Transformers Work (Gamer Edition) 🎮

Imagine you're playing an epic co-op multiplayer game with a squad of 100 players, and everyone is trying to solve a giant riddle together:

- **Old-School Bots (RNNs)**: Read words one by one in a slow line. By the time they reached word 50, they totally forgot what word 2 was doing!
- **Self-Attention Mechanism (The Radar Ping)**: When a Transformer sees a word (like *"bank"*), it sends a radar ping to every single other word in the entire sentence at the exact same millisecond. If it sees *"river"*, it instantly knows it means water, not money!
- **Multi-Head Attention (The Squad Mini-Map)**: Instead of looking with just one eye, the Transformer has 8 to 96 different "heads" looking at grammar, feelings, facts, and rhymes all simultaneously.
- **Feed-Forward Layers (The Power-Up Station)**: Each word takes what it learned from its squad and upgrades its meaning into high-dimensional math tensors.`;
    }

    if (p.includes('email') || p.includes('executive') || p.includes('feedback')) {
      return `### Executive Feedback Request Draft

**Subject**: Strategic Review: [Project Name] Key Milestone Feedback

Dear [Executive Name],

I hope this week is treating you well.

Our team has completed the milestone phase for **[Project Name]**, focusing on optimizing operational throughput and user onboarding efficiency.

Given your strategic perspective on this quarter's growth priorities, I would greatly value 10 minutes of your targeted insights on two key decisions:
- The updated architectural roadmap and vendor consolidation timeline
- The key performance indicators established for user retention

A brief 1-page executive briefing memo is attached for your quick review. If your calendar permits, could we reserve 15 minutes next Tuesday afternoon or Thursday morning?

Thank you for your time and continued guidance.

Warm regards,  
**[Your Name]**  
[Your Role / Team]`;
    }

    if (p.includes('async') || p.includes('race condition') || p.includes('debug')) {
      return `### Debugging JavaScript Async/Await Race Condition

A common cause of race conditions occurs when multiple asynchronous requests fire rapidly (e.g., fast user clicks or search keystrokes) and resolve in non-deterministic order.

\`\`\`javascript
// ❌ Buggy: Older query might resolve AFTER newer query
async function handleSearch(query) {
  const data = await fetchResults(query);
  updateUI(data); // Race condition hazard!
}

// ✅ Correct Fix: AbortController Pattern
let currentAbortController = null;

async function handleSearchSafe(query) {
  // Cancel previous pending network dispatch
  if (currentAbortController) {
    currentAbortController.abort();
  }
  currentAbortController = new AbortController();

  try {
    const response = await fetch(\`/api/search?q=\${encodeURIComponent(query)}\`, {
      signal: currentAbortController.signal
    });
    const results = await response.json();
    updateUI(results);
  } catch (err) {
    if (err.name !== 'AbortError') {
      console.error('Unhandled search failure:', err);
    }
  }
}
\`\`\`

**Resolution Summary**:
- Instantiates an \`AbortController\` before each invocation
- Actively cancels in-flight previous promises, guaranteeing only the latest user action renders to the UI.`;
    }

    // Dynamic Default Response for any prompt
    const modeDesc = mode === 'CREATIVE' 
      ? 'Synthesized with high creative divergence and lateral associative pathways.' 
      : mode === 'PRECISE' 
      ? 'Computed with deterministic analytical precision and verified logic axioms.' 
      : 'Formulated with balanced cognitive parameters and structured clarity.';

    return `### Nexus Neural Analysis

${modeDesc}

**Direct Inquiry**: "${escapeHtml(prompt)}"

1. **Primary Insight**: The core premise involves structured computational dynamics. By prioritizing core modularity and clean abstractions, scalable outcomes are reliably attained.
2. **Actionable Implementation**:
   - Establish strict boundary conditions early in the execution lifecycle.
   - Decouple state mutations from rendering pipelines to eliminate unnecessary overhead.
   - Leverage asynchronous telemetry to measure performance latency in real time.

\`\`\`json
{
  "system": "Nexus-Cortex-4",
  "status": "Optimal",
  "executionMode": "${mode}",
  "confidenceScore": 0.984
}
\`\`\`

Feel free to refine this query with additional specific constraints or tap a quick chip above!`;
  }

  // =========================================================================
  // Provider: Google Gemini API
  // =========================================================================
  async function callGeminiApi(prompt, mode, apiKey) {
    const temperature = mode === 'CREATIVE' ? 0.9 : mode === 'PRECISE' ? 0.2 : 0.7;
    const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`;

    const requestBody = {
      contents: [{
        parts: [{ text: prompt }]
      }],
      generationConfig: {
        temperature: temperature,
        maxOutputTokens: 2048
      }
    };

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      const errJson = await response.json().catch(() => ({}));
      throw new Error(errJson.error?.message || `Gemini API error: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    const candidate = data.candidates?.[0];
    const text = candidate?.content?.parts?.[0]?.text;

    if (!text) {
      throw new Error('Gemini API returned an empty response.');
    }

    return text;
  }

  // =========================================================================
  // Provider: Custom OpenAI-Compatible
  // =========================================================================
  async function callCustomEndpoint(prompt, mode, endpoint, apiKey) {
    const temperature = mode === 'CREATIVE' ? 0.9 : mode === 'PRECISE' ? 0.2 : 0.7;

    const response = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiKey}`
      },
      body: JSON.stringify({
        model: 'gpt-4o-mini',
        messages: [{ role: 'user', content: prompt }],
        temperature: temperature
      })
    });

    if (!response.ok) {
      const err = await response.text().catch(() => '');
      throw new Error(`Custom API returned error ${response.status}: ${err}`);
    }

    const data = await response.json();
    return data.choices?.[0]?.message?.content || 'No text content returned.';
  }

  // =========================================================================
  // Response Markdown Rendering
  // =========================================================================
  function renderResponse(markdown, elapsedSeconds) {
    elements.loadingView.classList.add('hidden');
    elements.successView.classList.remove('hidden');

    // Duration Badge
    elements.durationBadge.textContent = `Generated in ${elapsedSeconds}s`;
    elements.durationBadge.classList.remove('hidden');

    // Metrics
    const words = markdown.trim().split(/\s+/).filter(Boolean).length;
    const chars = markdown.length;
    elements.responseMetrics.textContent = `${words} words • ${chars} chars`;

    // Render HTML
    elements.responseBody.innerHTML = parseMarkdown(markdown);

    // Attach copy listeners to individual code blocks
    elements.responseBody.querySelectorAll('.code-copy-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const codeElement = btn.closest('.code-container').querySelector('pre');
        if (codeElement) {
          navigator.clipboard.writeText(codeElement.innerText).then(() => {
            btn.classList.add('copied');
            btn.innerHTML = '<span>Copied!</span>';
            setTimeout(() => {
              btn.classList.remove('copied');
              btn.innerHTML = '<span>Copy</span>';
            }, 2000);
          });
        }
      });
    });

    // Reset Copy and Speech Buttons
    resetCopyBtn();
    resetSpeakBtn();
  }

  function showError(msg) {
    elements.loadingView.classList.add('hidden');
    elements.errorView.classList.remove('hidden');
    elements.errorMessage.textContent = msg;
  }

  // Custom Fast Markdown Parser
  function parseMarkdown(md) {
    // Escape standard HTML first
    let raw = escapeHtml(md);

    // Code blocks with language detection
    raw = raw.replace(/```(\w*)\n([\s\S]*?)```/g, (match, lang, code) => {
      const displayLang = lang.trim() || 'code';
      return `
        <div class="code-container">
          <div class="code-header">
            <span class="code-lang">${displayLang.toUpperCase()}</span>
            <button class="code-copy-btn" type="button"><span>Copy</span></button>
          </div>
          <div class="code-scroll">
            <pre><code>${code.trim()}</code></pre>
          </div>
        </div>
      `;
    });

    // Headers
    raw = raw.replace(/^### (.*$)/gim, '<h3>$1</h3>');
    raw = raw.replace(/^## (.*$)/gim, '<h2>$1</h2>');
    raw = raw.replace(/^# (.*$)/gim, '<h1>$1</h1>');

    // Bold & Italics
    raw = raw.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    raw = raw.replace(/\*(.*?)\*/g, '<em>$1</em>');

    // Inline code
    raw = raw.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>');

    // Bullet points: lines beginning with "- " or "* "
    raw = raw.replace(/^[-*]\s+(.*$)/gim, '<li class="bullet-item"><span class="bullet-dot">•</span><div>$1</div></li>');

    // Numbered points: lines beginning with "1. " etc.
    raw = raw.replace(/^(\d+)\.\s+(.*$)/gim, '<li class="number-item"><span class="number-prefix">$1.</span><div>$2</div></li>');

    // Paragraph breaks
    const paragraphs = raw.split(/\n{2,}/).map(chunk => {
      chunk = chunk.trim();
      if (!chunk) return '';
      if (chunk.startsWith('<h') || chunk.startsWith('<div class="code-container') || chunk.startsWith('<li')) {
        return chunk;
      }
      return `<p>${chunk.replace(/\n/g, '<br>')}</p>`;
    });

    return paragraphs.join('\n');
  }

  function escapeHtml(str) {
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  // =========================================================================
  // Text to Speech (SpeechSynthesis API)
  // =========================================================================
  function toggleSpeech() {
    if (!('speechSynthesis' in window)) {
      showToast('Speech synthesis not supported on this browser');
      return;
    }

    if (state.isSpeaking) {
      stopSpeech();
    } else {
      startSpeech();
    }
  }

  function startSpeech() {
    if (!state.rawResponse) return;

    // Clean markdown for natural reading
    const cleanText = state.rawResponse
      .replace(/```[\s\S]*?```/g, 'Code block omitted.')
      .replace(/#{1,6}\s?/g, '')
      .replace(/\*\*/g, '')
      .replace(/`/g, '')
      .replace(/[-*]\s/g, '');

    window.speechSynthesis.cancel();
    speechUtterance = new SpeechSynthesisUtterance(cleanText);
    speechUtterance.rate = 1.0;
    speechUtterance.pitch = 1.0;

    speechUtterance.onstart = () => {
      state.isSpeaking = true;
      elements.speakBtn.classList.add('active');
      elements.speakBtnText.textContent = 'Stop';
      elements.iconSpeak.classList.add('hidden');
      elements.iconStop.classList.remove('hidden');
    };

    speechUtterance.onend = resetSpeakBtn;
    speechUtterance.onerror = resetSpeakBtn;

    window.speechSynthesis.speak(speechUtterance);
  }

  function stopSpeech() {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
    }
    resetSpeakBtn();
  }

  function resetSpeakBtn() {
    state.isSpeaking = false;
    elements.speakBtn.classList.remove('active');
    elements.speakBtnText.textContent = 'Speak';
    elements.iconSpeak.classList.remove('hidden');
    elements.iconStop.classList.add('hidden');
  }

  // =========================================================================
  // Clipboard Operations
  // =========================================================================
  function copyFullResponse() {
    if (!state.rawResponse) return;

    navigator.clipboard.writeText(state.rawResponse).then(() => {
      elements.copyBtn.classList.add('active');
      elements.copyBtnText.textContent = 'Copied';
      elements.iconCopy.classList.add('hidden');
      elements.iconCopied.classList.remove('hidden');
      showToast('Full response copied to clipboard');

      setTimeout(resetCopyBtn, 2200);
    }).catch(err => {
      console.warn('Clipboard write error:', err);
      showToast('Unable to access clipboard');
    });
  }

  function resetCopyBtn() {
    elements.copyBtn.classList.remove('active');
    elements.copyBtnText.textContent = 'Copy';
    elements.iconCopy.classList.remove('hidden');
    elements.iconCopied.classList.add('hidden');
  }

  // =========================================================================
  // Modal Operations
  // =========================================================================
  function openSettingsModal() {
    elements.settingsModal.classList.remove('hidden');
  }

  function closeSettingsModal() {
    elements.settingsModal.classList.add('hidden');
  }

  // =========================================================================
  // Toast Notification
  // =========================================================================
  function showToast(message) {
    if (toastTimer) clearTimeout(toastTimer);
    elements.toastMessage.textContent = message;
    elements.toast.classList.remove('hidden');

    toastTimer = setTimeout(() => {
      elements.toast.classList.add('hidden');
    }, 2500);
  }

  // Bootstrap when ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
