# PDFInjector
PDFInjector is a commandline utility takes a PDF file and JSON data as input, and generates a new PDF, filling its forms with the data from the JSON.

## Usage
1. Clone the project and compile it into a jar OR download the jar directly from the releases page.
2. run with `java -jar pdfinjector.jar -h` to show a list of flags.

## Flags
* -h Show this help message.
* -i Input PDF file path, template PDF.
* -o Output file path, a new PDF document is created here.
* -d Data. Either in JSON format, or the path to a json file.
* -p Populate the PDF with the names of each form. Run this first to find

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
PDFInjector uses the following libraries
1. iTextPDF under AGPL
2. minimal-json under MIT
