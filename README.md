# PDFInjector
PDFInjector is a commandline utility takes a PDF file and JSON data as input, and generates a new PDF, filling its forms with the data from the JSON. The new PDF is saved to the output directory specified by `-o` along with `summary.txt` which shows the fields that were successfully filled.

## Usage
1. Clone the project and compile it into a jar OR download the jar directly from the releases page.
2. run with `java -jar pdfinjector.jar -h` to show a list of flags.

## Flags
* -h Show this help message.
* -i Input PDF file path, template PDF.
* -o Output file path, a new PDF document is created here.
* -d Data. Either in JSON format, or the path to a json file.
* -p Populate the PDF with the names of each form. Run this first to find

## Example
1. Filling out the fields with their names.
```
$ java -jar pdfinjector.jar -i /path/to/document.pdf -o /path/to/destination/output.pdf -p
```

2. Filling out the fields from a json file
```
$ java -jar pdfinjector.jar -i /path/to/document.pdf -o /path/to/destination/output.pdf -d /path/to/data.json
```

3. Filling out the fields from direct json data
```
$ java -jar pdfinjector.jar -i /path/to/document.pdf -o /path/to/destination/output.pdf -d {\"fields\":[{\"key\":\"name-field\",\"value\":\"John Doe\"}]}
```

## JSON Format
The JSON must have quotation marks around both the keys and values. All keys and values will be read from the `fields` array.
For example:
```
{
  "fields":[
    {
      "key":"name-field",
      "value":"John Doe"
    }
  ]
}
```

## License
PDFInjector uses the following third-party libraries
* iTextPDF under AGPL
* minimal-json under MIT
