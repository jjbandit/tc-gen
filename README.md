
# Preamble
This is a tool I built to quickly generate CRD Timecards.
## File List
- LICENSE
- README.md
- genericTemplate.xlsx
- exampleTemplate.xlsx
- tc-gen-v0.2.jar

# Usage
To start generating timecards first you need a template file to point
the program towards.  A blank template workbook is provided in the
archive you downloaded.

## Build your Templates
Open the genericTemplate.xlsx workbook file with Excel. It is
a good idea to saveAs -> 'yourSuperDuperTemplate' at this point to keep
the generic file intact. The template file should contain a blank timecard
sheet which you may copy/rename/edit as you please.

## An example
To better understand what you might want in your template file, let's consider
a real world example:

An aquatics department can categorize its staff members into:

- Lifeguard 1
- Lifeguard 2
- Aquafit Instructors

To achieve this:

1. In Excel, open the genericTemplate.xlsx file and
saveAs -> 'yourDepartmentTemplate'
2. From our new template copy the 'Blank' sheet twice and rename
them to 'Lifeguard 1', 'Lifeguard 2' and 'Aquafit'
3. Input the appropriate pay grades into column C
4. Save and close Excel
5. Now we can fire up the tcGen-v0.2.jar file and point it toward
our new template file
using the 'Open Template' button.
6. You can now add and remove employees to each template group

See the exampleTemplate.xlsx file for a demonstration.

# Troubleshooting
Q: Employees I add are not saved when I open the template again.

A: The tcGen program saves the employee data you enter into it to a
hidden 'Roster' sheet inside your excel Template. Make sure you are
using the 'Save & Close' button, and that the template workbook
is not open in Excel.

# Shameless Self Promotion
Like this software? Consider [contributing](http://jessejames.ca/projects/tc-gen)
to its development.

Like the way I do business? Contact me by email at jesse.hughes.it@gmail.com

# License
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

Copyright (C) 2015 Jesse Hughes
