import re

file_path = '/var/www/html/TPRS/admin-dashboard.html'
with open(file_path, 'r') as f:
    text = f.read()

# Replace light mode colors with dark mode colors in the Settings section
# 1. Primary borders & titles
text = text.replace('border-bottom:1px solid #eee;', 'border-bottom:1px solid #3d3d52;')
text = text.replace('color:#666;', 'color:#b5b5cc;')

# 2. Internal panel backgrounds and borders
text = text.replace('background:#f9f9f9;', 'background:#323248;')
text = text.replace('border:1px solid #e0e0e0;', 'border:1px solid #3d3d52; box-shadow: 0 2px 12px rgba(0,0,0,0.1);')

# 3. Label colors and nested borders
text = text.replace('color:#333;', 'color:#e2e2ea;')
text = text.replace('border-bottom:1px solid #ddd;', 'border-bottom:1px solid #3d3d52;')

# 4. Table header texts (ID | Name)
text = text.replace('color:#777;', 'color:#b5b5cc;')

# 5. Fix JS inputs styling
old_inputs = """function createInputsHtml(inputs) {
            return inputs.map(i => `<input type="text" style="flex:${i.flex}; font-size:0.95rem; padding:0.6rem; border:1px solid #ccc; border-radius:6px; outline:none; transition:border-color 0.2s; box-shadow:inset 0 1px 3px rgba(0,0,0,0.05);" class="${i.class}" value="${i.val}" placeholder="${i.placeholder}" onfocus="this.style.borderColor='#2196F3'" onblur="this.style.borderColor='#ccc'">`).join('');
        }"""
        
new_inputs = """function createInputsHtml(inputs) {
            return inputs.map(i => `<input type="text" style="flex:${i.flex}; font-size:0.95rem; padding:0.6rem; border:1px solid #3d3d52; border-radius:6px; outline:none; background:#252538; color:#e2e2ea; transition:border-color 0.2s; box-shadow:inset 0 1px 3px rgba(0,0,0,0.2);" class="${i.class}" value="${i.val}" placeholder="${i.placeholder}" onfocus="this.style.borderColor='#e84393'" onblur="this.style.borderColor='#3d3d52'">`).join('');
        }"""

text = text.replace(old_inputs, new_inputs)

# 6. Set the drag_indicator icon color from dark gray to light gray so it's visible on dark bg
text = text.replace('class="material-icons" style="color:#666; cursor:grab; font-size:1.2rem;">drag_indicator</span>', 'class="material-icons" style="color:#b5b5cc; cursor:grab; font-size:1.2rem;">drag_indicator</span>')
text = text.replace('const box = sibling.getBoundingClientRect();', 'const box = sibling.getBoundingClientRect();') # dummy

with open(file_path, 'w') as f:
    f.write(text)

print("Updated Settings UI to match Dark Theme correctly.")
