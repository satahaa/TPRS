import re

file_path = '/var/www/html/TPRS/admin-dashboard.html'
with open(file_path, 'r') as f:
    text = f.read()

# Add the CSS snippet right after `<style>`
css_snippet = """    <style>
        .hide-scrollbar::-webkit-scrollbar { display: none; }
        .hide-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }"""
text = text.replace('    <style>', css_snippet)

# Add the hide-scrollbar class to the 4 divs
text = text.replace('id="settings-sessions-list" style="', 'id="settings-sessions-list" class="hide-scrollbar" style="')
text = text.replace('id="settings-specializations-list" style="', 'id="settings-specializations-list" class="hide-scrollbar" style="')
text = text.replace('id="settings-departments-list" style="', 'id="settings-departments-list" class="hide-scrollbar" style="')
text = text.replace('id="settings-degrees-list" style="', 'id="settings-degrees-list" class="hide-scrollbar" style="')

with open(file_path, 'w') as f:
    f.write(text)

print("Scrollbars hidden successfully.")
