import re

file_path = '/var/www/html/TPRS/script.js'
with open(file_path, 'r') as f:
    text = f.read()

# Replace the title setup
old_title_logic = """        } else if (activeTypeFilter === 'thesis') {
            sectionTitle.textContent = 'Thesis';
        } else if (activeTypeFilter === 'project') {
            sectionTitle.textContent = 'Projects';
        } else {"""
        
new_title_logic = """        } else if (activeTypeFilter === 'thesis') {
            sectionTitle.innerHTML = '<span class="material-icons" style="cursor:pointer;margin-right:0.4rem;vertical-align:middle;color:#2196F3;" onclick="exitTypeFilter()">arrow_back</span> Thesis Filter';
        } else if (activeTypeFilter === 'project') {
            sectionTitle.innerHTML = '<span class="material-icons" style="cursor:pointer;margin-right:0.4rem;vertical-align:middle;color:#ff9800;" onclick="exitTypeFilter()">arrow_back</span> Projects Filter';
        } else {"""

text = text.replace(old_title_logic, new_title_logic)

# Add exitTypeFilter function
exit_func = """
function exitTypeFilter() {
    activeTypeFilter = '';
    applyFilters();
}
"""

if "function exitTypeFilter" not in text:
    text += exit_func

with open(file_path, 'w') as f:
    f.write(text)

print("Back buttons added successfully.")
