// =====================================================
// MOCK DATA - SAMPLE THESES (FALLBACK DATA)
// =====================================================
let thesesData = [
    {
        id: 1,
        title: "Smart Medical Record Management System",
        author: "Sumayea Akter",
        authorInitials: "SA",
        department: "CSE",
        degree: "Bachelor",
        year: 2022,
        session: "2022-2023",
        field: "Healthcare IT",
        views: 245,
        bookmarked: false,
        supervisor: "A S M Delowar Hossain"
    },
    {
        id: 2,
        title: "Shape Ditector",
        author: "Mst. Joba Sarkar",
        authorInitials: "JS",
        department: "CSE",
        degree: "Bachelor",
        year: 2022,
        session: "2022-2023",
        field: "Computer Vision",
        views: 189,
        bookmarked: false,
        supervisor: "A S M Delowar Hossain"
    },
    {
        id: 3,
        title: "Virtual Study Assistant",
        author: "Juthi Basak",
        authorInitials: "JB",
        department: "CSE",
        degree: "Master",
        year: 2023,
        session: "2023-2024",
        field: "Machine Learning",
        views: 312,
        bookmarked: false,
        supervisor: "A S M Delowar Hossain"
    },
    {
        id: 4,
        title: "Waste Management System",
        author: "Md. Niamul Islam Mahin",
        authorInitials: "MN",
        department: "ICT",
        degree: "Bachelor",
        year: 2022,
        session: "2022-2023",
        field: "IoT & Sensors",
        views: 156,
        bookmarked: false,
        supervisor: "Dr. Md. Sazzad Hossain"
    },
    {
        id: 5,
        title: "Simple Imperative Language Compiler",
        author: "S A Tahaa",
        authorInitials: "ST",
        department: "CSE",
        degree: "Bachelor",
        year: 2024,
        session: "2024-2025",
        field: "Compiler Design",
        views: 189,
        bookmarked: false,
        supervisor: "Dr. Mehedi Hasan Talukder"
    }
];

// Popular keywords
let keywords = [
    { name: "Machine Learning", count: 28 },
    { name: "Blockchain", count: 15 },
    { name: "Smart Grid", count: 12 },
    { name: "Others", count: 34 }
];

// =====================================================
// CHECK AUTHENTICATION
// =====================================================
function checkAuth() {
    // Use TPRSApi if available, fallback to sessionStorage
    if (typeof TPRSApi !== 'undefined' && !TPRSApi.isLoggedIn()) {
        window.location.href = 'login.html';
        return false;
    } else if (typeof TPRSApi === 'undefined' && sessionStorage.getItem('isLoggedIn') !== 'true') {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// =====================================================
// LOAD DATA FROM BACKEND
// =====================================================
async function loadDataFromBackend() {
    try {
        // Try to get recent projects from backend
        if (typeof TPRSApi !== 'undefined') {
            const recentResult = await TPRSApi.getDashboardRecent(10);
            if (recentResult.success && recentResult.projects && recentResult.projects.length > 0) {
                // Convert backend data to display format
                thesesData = recentResult.projects.map(project => ({
                    id: project.id,
                    title: project.title,
                    author: project.studentName || project.authorName || 'Unknown',
                    authorInitials: getInitials(project.studentName || project.authorName || 'Unknown'),
                    department: project.department || 'CSE',
                    degree: project.degree || 'Bachelor',
                    year: new Date(project.createdAt || project.submittedAt).getFullYear(),
                    session: project.session || '2024-2025',
                    field: project.type || 'Thesis',
                    views: project.views || 0,
                    bookmarked: false,
                    supervisor: project.supervisorName || project.supervisor || 'N/A',
                    status: project.status || 'pending'
                }));
            }

            // Get dashboard stats
            const statsResult = await TPRSApi.getDashboardStats();
            if (statsResult.success && statsResult.stats) {
                document.getElementById('totalThesis').textContent = statsResult.stats.totalThesis || 0;
                document.getElementById('totalProject').textContent = statsResult.stats.totalProject || 0;
                document.getElementById('totalAuthors').textContent = statsResult.stats.totalAuthors || 0;
            }
        }
    } catch (error) {
        console.log('Backend not available, using local data');
        // Load from localStorage as fallback
        loadLocalStorageData();
    }
}

// Load data from localStorage (fallback)
function loadLocalStorageData() {
    const submissions = JSON.parse(localStorage.getItem('thesisSubmissions') || '[]');
    if (submissions.length > 0) {
        const localTheses = submissions.map((sub, index) => ({
            id: sub.id || (1000 + index),
            title: sub.title,
            author: sub.authorName,
            authorInitials: getInitials(sub.authorName),
            department: sub.department || 'CSE',
            degree: sub.degree || 'Bachelor',
            year: new Date(sub.submittedAt).getFullYear(),
            session: sub.session || '2024-2025',
            field: sub.type || 'Thesis',
            views: 0,
            bookmarked: false,
            supervisor: sub.supervisor || 'N/A',
            status: sub.status || 'pending'
        }));
        
        // Merge with existing data (recent first)
        thesesData = [...localTheses.reverse(), ...thesesData];
    }
}

// Helper function to get initials
function getInitials(name) {
    if (!name) return 'NA';
    const parts = name.split(' ');
    if (parts.length >= 2) {
        return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
}

// =====================================================
// STATE MANAGEMENT
// =====================================================
let displayedTheses = [...thesesData];
let filters = {
    sessions: ["2024-2025"],
    degrees: [],
    author: "",
    supervisor: "",
    keyword: ""
};

// =====================================================
// DOM ELEMENTS
// =====================================================
const thesisListEl = document.getElementById("thesisList");
const searchInput = document.getElementById("searchInput");
const searchBtn = document.querySelector(".search-btn");
const authorFilterInput = document.getElementById("authorFilter");
const supervisorFilterSelect = document.getElementById("supervisorFilter");
const sessionFilterGroup = document.getElementById("sessionFilter");
const degreeFilterGroup = document.getElementById("degreeFilter");
const keywordsListEl = document.getElementById("keywordsList");
const totalThesisEl = document.getElementById("totalThesis");
const totalAuthorsEl = document.getElementById("totalAuthors");

// =====================================================
// RENDER FUNCTIONS
// =====================================================

/**
 * Render the thesis list based on current filters and search
 */
function renderThesisList() {
    if (displayedTheses.length === 0) {
        thesisListEl.innerHTML = '<div class="no-results">No theses found matching your search criteria.</div>';
        return;
    }

    thesisListEl.innerHTML = displayedTheses.map(thesis => `
        <div class="thesis-card" data-thesis-id="${thesis.id}">
            <span class="material-icons thesis-icon">description</span>
            <div class="thesis-content">
                <div class="thesis-title">${thesis.title}</div>
                <div class="thesis-meta">
                    <div class="author-info">
                        <div class="author-avatar">${thesis.authorInitials}</div>
                        <span>${thesis.author}</span>
                    </div>
                    <span class="meta-tag">${thesis.department}</span>
                    <span class="meta-tag">${thesis.degree}</span>
                    <span class="meta-tag">${thesis.year}</span>
                    <span class="meta-tag">${thesis.field}</span>
                </div>
            </div>
            <div class="thesis-actions">
                <div class="views-count">
                    <span class="material-icons" style="font-size: 1rem;">visibility</span>
                    ${thesis.views}
                </div>
                <button class="bookmark-btn ${thesis.bookmarked ? 'active' : ''}" data-thesis-id="${thesis.id}">
                    <span class="material-icons">${thesis.bookmarked ? 'bookmark' : 'bookmark_border'}</span>
                </button>
            </div>
        </div>
    `).join("");

    // Add event listeners to bookmark buttons
    document.querySelectorAll(".bookmark-btn").forEach(btn => {
        btn.addEventListener("click", handleBookmarkClick);
    });

    // Update stats
    updateStats();
}

/**
 * Render the keywords list
 */
function renderKeywords() {
    keywordsListEl.innerHTML = keywords.map((kw, index) => `
        <div class="keyword-item ${filters.keyword === kw.name ? 'active' : ''}" data-keyword="${kw.name}">
            <div class="keyword-label">
                <span class="material-icons keyword-icon">
                    ${index === 0 ? 'auto_awesome' : index === 1 ? 'link' : index === 2 ? 'electric_bolt' : 'label'}
                </span>
                ${kw.name}
            </div>
            <span class="keyword-count">${kw.count}</span>
        </div>
    `).join("");

    // Add event listeners to keyword items
    document.querySelectorAll(".keyword-item").forEach(item => {
        item.addEventListener("click", handleKeywordClick);
    });
}

/**
 * Update statistics on the right sidebar
 */
function updateStats() {
    totalThesisEl.textContent = displayedTheses.length;
    
    // Count unique authors
    const uniqueAuthors = new Set(displayedTheses.map(t => t.author)).size;
    totalAuthorsEl.textContent = uniqueAuthors;
}

// =====================================================
// FILTER FUNCTIONS
// =====================================================

/**
 * Apply all active filters to the thesis data
 */
function applyFilters() {
    displayedTheses = thesesData.filter(thesis => {
        // Session filter
        if (filters.sessions.length > 0 && !filters.sessions.includes(thesis.session)) {
            return false;
        }

        // Degree filter
        if (filters.degrees.length > 0 && !filters.degrees.includes(thesis.degree)) {
            return false;
        }

        // Author filter
        if (filters.author && !thesis.author.toLowerCase().includes(filters.author.toLowerCase())) {
            return false;
        }

        // Supervisor filter
        if (filters.supervisor && thesis.supervisor !== filters.supervisor) {
            return false;
        }

        // Search filter
        if (filters.search && !thesis.title.toLowerCase().includes(filters.search.toLowerCase())) {
            return false;
        }

        // Keyword filter
        if (filters.keyword && thesis.field !== filters.keyword) {
            return false;
        }

        return true;
    });

    renderThesisList();
}

/**
 * Handle session checkbox changes
 */
sessionFilterGroup.addEventListener("change", (e) => {
    if (e.target.type === "checkbox") {
        const checked = Array.from(sessionFilterGroup.querySelectorAll("input:checked"))
            .map(input => input.value);
        filters.sessions = checked.length > 0 ? checked : [];
        applyFilters();
    }
});

/**
 * Handle degree checkbox changes
 */
degreeFilterGroup.addEventListener("change", (e) => {
    if (e.target.type === "checkbox") {
        const checked = Array.from(degreeFilterGroup.querySelectorAll("input:checked"))
            .map(input => input.value);
        filters.degrees = checked;
        applyFilters();
    }
});

/**
 * Handle author filter input
 */
authorFilterInput.addEventListener("input", (e) => {
    filters.author = e.target.value;
    applyFilters();
});

/**
 * Handle supervisor dropdown change
 */
supervisorFilterSelect.addEventListener("change", (e) => {
    filters.supervisor = e.target.value;
    applyFilters();
});

/**
 * Handle search input
 */
searchInput.addEventListener("input", (e) => {
    filters.search = e.target.value;
    applyFilters();
});

/**
 * Handle search button click
 */
searchBtn.addEventListener("click", () => {
    applyFilters();
});

/**
 * Handle Enter key in search input
 */
searchInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
        applyFilters();
    }
});

// =====================================================
// INTERACTION FUNCTIONS
// =====================================================

/**
 * Handle bookmark button clicks
 */
function handleBookmarkClick(e) {
    e.stopPropagation();
    const thesisId = parseInt(e.currentTarget.dataset.thesisId);
    const thesis = thesesData.find(t => t.id === thesisId);
    
    if (thesis) {
        thesis.bookmarked = !thesis.bookmarked;
        renderThesisList();
    }
}

/**
 * Handle keyword item clicks
 */
function handleKeywordClick(e) {
    const keyword = e.currentTarget.dataset.keyword;
    
    // Toggle keyword filter
    if (filters.keyword === keyword) {
        filters.keyword = "";
    } else {
        filters.keyword = keyword;
    }
    
    applyFilters();
    renderKeywords();
}

/**
 * Handle profile dropdown menu
 */
function setupProfileDropdown() {
    const userProfile = document.getElementById("userProfile");
    const profileDropdown = document.getElementById("profileDropdown");
    
    if (!userProfile || !profileDropdown) return;
    
    // Update profile display with current user data
    updateProfileDisplay();
    
    // Toggle dropdown on profile click
    userProfile.addEventListener("click", (e) => {
        e.stopPropagation();
        userProfile.classList.toggle("active");
    });
    
    // Close dropdown when clicking outside
    document.addEventListener("click", (e) => {
        if (!userProfile.contains(e.target)) {
            userProfile.classList.remove("active");
        }
    });
    
    // Handle dropdown item clicks
    const dropdownItems = profileDropdown.querySelectorAll(".dropdown-item");
    dropdownItems.forEach(item => {
        item.addEventListener("click", (e) => {
            // Only close dropdown for non-button items (links)
            if (item.tagName !== "BUTTON") {
                setTimeout(() => {
                    userProfile.classList.remove("active");
                }, 100);
            }
        });
    });
    
    // Handle logout button
    const logoutBtn = profileDropdown.querySelector(".logout-btn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            // Use TPRSApi if available
            if (typeof TPRSApi !== 'undefined') {
                TPRSApi.logout();
            } else {
                sessionStorage.removeItem('isLoggedIn');
                sessionStorage.removeItem('userEmail');
                sessionStorage.removeItem('currentUser');
            }
            window.location.href = 'login.html';
        });
    }
}

/**
 * Update profile display with current user data
 */
function updateProfileDisplay() {
    let currentUser = null;
    
    // Get current user from TPRSApi or sessionStorage
    if (typeof TPRSApi !== 'undefined') {
        currentUser = TPRSApi.getCurrentUser();
    }
    if (!currentUser) {
        currentUser = JSON.parse(sessionStorage.getItem('currentUser') || 'null');
    }
    
    if (currentUser) {
        const fullName = `${currentUser.firstName || ''} ${currentUser.lastName || ''}`.trim() || 'User';
        const initials = getInitials(fullName);
        const email = currentUser.email || '';
        const department = currentUser.department || 'N/A';
        const studentId = currentUser.studentId || currentUser.id || 'N/A';
        
        // Update header profile
        const userAvatar = document.querySelector('.user-avatar');
        const userName = document.querySelector('.user-name');
        if (userAvatar) userAvatar.textContent = initials;
        if (userName) userName.textContent = fullName;
        
        // Update dropdown profile
        const dropdownAvatar = document.querySelector('.dropdown-avatar');
        const dropdownName = document.querySelector('.dropdown-name');
        const dropdownEmail = document.querySelector('.dropdown-email');
        const departmentItem = document.querySelectorAll('.dropdown-detail-item')[0];
        const studentIdItem = document.querySelectorAll('.dropdown-detail-item')[1];
        
        if (dropdownAvatar) dropdownAvatar.textContent = initials;
        if (dropdownName) dropdownName.textContent = fullName;
        if (dropdownEmail) dropdownEmail.textContent = email;
        if (departmentItem) {
            departmentItem.innerHTML = `<span class="material-icons">business</span>${department} Department`;
        }
        if (studentIdItem) {
            studentIdItem.innerHTML = `<span class="material-icons">badge</span>${studentId}`;
        }
    }
}

/**
 * Extract keywords from text
 */
function extractKeywords(text) {
    // Remove special characters and split into words
    const words = text.toLowerCase()
        .replace(/[^a-z0-9\s]/g, '')
        .split(/\s+/)
        .filter(word => word.length > 0);
    
    return words;
}

/**
 * Build enhanced search index with keywords
 */
function buildSearchIndex() {
    return thesesData.map(thesis => ({
        id: thesis.id,
        title: thesis.title,
        author: thesis.author,
        field: thesis.field,
        type: "Thesis",
        keywords: extractKeywords(thesis.title),
        fieldKeywords: extractKeywords(thesis.field),
        allKeywords: [
            ...extractKeywords(thesis.title),
            ...extractKeywords(thesis.field),
            ...extractKeywords(thesis.author)
        ]
    }));
}

/**
 * Calculate relevance score for keyword-based matching
 */
function calculateKeywordRelevance(queryKeywords, item) {
    let score = 0;
    const titleLower = item.title.toLowerCase();
    const fieldLower = item.field.toLowerCase();
    
    // Score based on keyword matches
    let exactKeywordMatches = 0;
    let partialKeywordMatches = 0;
    let fieldKeywordMatches = 0;
    
    queryKeywords.forEach(queryKeyword => {
        // Exact keyword match in title
        if (item.keywords.includes(queryKeyword)) {
            exactKeywordMatches++;
            score += 300;
        }
        // Partial keyword match in title (keyword contains query or query contains keyword)
        else if (item.keywords.some(kw => 
            kw.includes(queryKeyword) || queryKeyword.includes(kw)
        )) {
            partialKeywordMatches++;
            score += 150;
        }
        
        // Field keyword match
        if (item.fieldKeywords.includes(queryKeyword)) {
            fieldKeywordMatches++;
            score += 100;
        }
        // Partial field match
        else if (item.fieldKeywords.some(kw => 
            kw.includes(queryKeyword) || queryKeyword.includes(kw)
        )) {
            score += 50;
        }
        
        // Substring match in title
        if (titleLower.includes(queryKeyword)) {
            score += 80;
        }
    });
    
    // Bonus for matching all keywords
    if (queryKeywords.length > 0 && exactKeywordMatches === queryKeywords.length) {
        score += 500;
    }
    
    // Bonus for matching most keywords
    const keywordCoverageRatio = (exactKeywordMatches + partialKeywordMatches) / queryKeywords.length;
    if (keywordCoverageRatio >= 0.7) {
        score += 200;
    }
    
    return score;
}

/**
 * Get autocomplete suggestions based on keyword search
 */
function getKeywordSuggestions(query) {
    if (!query.trim()) {
        return [];
    }
    
    const queryKeywords = extractKeywords(query);
    
    if (queryKeywords.length === 0) {
        return [];
    }
    
    const searchIndex = buildSearchIndex();
    const scored = searchIndex
        .map(item => ({
            ...item,
            relevance: calculateKeywordRelevance(queryKeywords, item)
        }))
        .filter(item => item.relevance > 0)
        .sort((a, b) => b.relevance - a.relevance)
        .slice(0, 10); // Limit to 10 suggestions
    
    return scored;
}

/**
 * Calculate relevance score for autocomplete matching
 */
function calculateRelevance(query, title, field) {
    const lowerQuery = query.toLowerCase();
    const lowerTitle = title.toLowerCase();
    const lowerField = field.toLowerCase();
    
    let score = 0;
    
    // Exact match in title
    if (lowerTitle === lowerQuery) {
        score += 1000;
    }
    // Title starts with query
    else if (lowerTitle.startsWith(lowerQuery)) {
        score += 500;
    }
    // Query is a complete word in title
    else if (lowerTitle.includes(" " + lowerQuery)) {
        score += 300;
    }
    // Partial match in title
    else if (lowerTitle.includes(lowerQuery)) {
        score += 200;
    }
    
    // Match in field/keywords
    if (lowerField.includes(lowerQuery)) {
        score += 100;
    }
    
    // Match by word relevance (e.g., "management system" matches "Waste Management System")
    const queryWords = lowerQuery.split(" ");
    const titleWords = lowerTitle.split(" ");
    const matchingWords = queryWords.filter(word => 
        titleWords.some(titleWord => titleWord.includes(word))
    );
    score += matchingWords.length * 50;
    
    return score;
}

/**
 * Render autocomplete suggestions
 */
function renderAutocompleteSuggestions(suggestions, query) {
    const suggestionsList = document.getElementById("suggestionsList");
    
    if (suggestions.length === 0) {
        suggestionsList.innerHTML = `
            <div class="suggestion-item no-results">
                <span class="material-icons" style="font-size: 1.2rem;">search_off</span>
                <span>No results found for "${query}"</span>
            </div>
        `;
        return;
    }
    
    // Group suggestions by relevance tier
    const highRelevance = suggestions.filter(s => s.relevance >= 500);
    const mediumRelevance = suggestions.filter(s => s.relevance >= 200 && s.relevance < 500);
    const lowRelevance = suggestions.filter(s => s.relevance < 200);
    
    let html = "";
    
    // High relevance section
    if (highRelevance.length > 0) {
        highRelevance.forEach(suggestion => {
            html += `
                <div class="suggestion-item" data-search-id="${suggestion.id}" data-search-title="${suggestion.title}">
                    <span class="material-icons suggestion-icon">star</span>
                    <div class="suggestion-text">
                        <div class="suggestion-title">${highlightQuery(suggestion.title, query)}</div>
                        <div class="suggestion-meta">${suggestion.author} • ${suggestion.field}</div>
                    </div>
                    <span class="suggestion-match-badge">Best Match</span>
                </div>
            `;
        });
    }
    
    // Medium relevance section
    if (mediumRelevance.length > 0) {
        mediumRelevance.forEach(suggestion => {
            html += `
                <div class="suggestion-item" data-search-id="${suggestion.id}" data-search-title="${suggestion.title}">
                    <span class="material-icons suggestion-icon">description</span>
                    <div class="suggestion-text">
                        <div class="suggestion-title">${highlightQuery(suggestion.title, query)}</div>
                        <div class="suggestion-meta">${suggestion.author} • ${suggestion.field}</div>
                    </div>
                </div>
            `;
        });
    }
    
    // Low relevance section
    if (lowRelevance.length > 0) {
        lowRelevance.forEach(suggestion => {
            html += `
                <div class="suggestion-item" data-search-id="${suggestion.id}" data-search-title="${suggestion.title}">
                    <span class="material-icons suggestion-icon">find_in_page</span>
                    <div class="suggestion-text">
                        <div class="suggestion-title">${highlightQuery(suggestion.title, query)}</div>
                        <div class="suggestion-meta">${suggestion.author} • ${suggestion.field}</div>
                    </div>
                </div>
            `;
        });
    }
    
    suggestionsList.innerHTML = html;
    
    // Add click handlers to suggestions
    document.querySelectorAll(".suggestion-item:not(.no-results)").forEach(item => {
        item.addEventListener("click", handleSuggestionClick);
    });
}

/**
 * Highlight matching query in suggestion text
 */
function highlightQuery(text, query) {
    const regex = new RegExp(`(${query})`, "gi");
    return text.replace(regex, "<strong style='color: #667eea;'>$1</strong>");
}

/**
 * Handle suggestion click
 */
function handleSuggestionClick(e) {
    const title = e.currentTarget.dataset.searchTitle;
    searchInput.value = title;
    
    // Trigger search
    filters.search = title;
    applyFilters();
    
    // Clear suggestions by blurring the input
    setTimeout(() => {
        searchInput.blur();
    }, 100);
}

/**
 * Setup autocomplete search functionality
 */
function setupAutocompletSearch() {
    const searchInput = document.getElementById("searchInput");
    
    if (!searchInput) return;
    
    // Handle input event for real-time suggestions
    searchInput.addEventListener("input", (e) => {
        const query = e.target.value.trim();
        
        if (query.length === 0) {
            const suggestionsList = document.getElementById("suggestionsList");
            suggestionsList.innerHTML = "";
            return;
        }
        
        // Use keyword-based search for better results
        if (query.length >= 2) {
            const suggestions = getKeywordSuggestions(query);
            renderAutocompleteSuggestions(suggestions, query);
        }
    });
    
    // Handle Enter key press
    searchInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter") {
            filters.search = searchInput.value;
            applyFilters();
            searchInput.blur();
        }
    });
    
    // Clear suggestions when focus is lost
    searchInput.addEventListener("blur", () => {
        setTimeout(() => {
            document.getElementById("suggestionsList").innerHTML = "";
        }, 150);
    });
}

// =====================================================
// INITIALIZATION
// =====================================================

/**
 * Initialize the application
 */
async function init() {
    // Check if user is authenticated
    if (!checkAuth()) {
        return;
    }
    
    // Load data from backend (with localStorage fallback)
    await loadDataFromBackend();
    
    // Render UI components
    renderThesisList();
    renderKeywords();
    updateStats();
    setupProfileDropdown();
    setupAutocompletSearch();

    // Add smooth scroll behavior
    document.documentElement.style.scrollBehavior = "smooth";
}

// Run initialization when DOM is ready
if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
} else {
    init();
}
