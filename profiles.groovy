ant.copy(file: 'profiles.xml.template',
              tofile: 'profiles.xml',
              filtering: true) {
  filterset() {
    filter(token: 'PROJECT_HOME',
            value: new File('.').getCanonicalPath())
    }
  }
log.info('')
log.info('This is the first time you invoked Maven for this project.')
log.info('Initialized profiles.xml. Finished setup, exiting.')
log.info('You can now use maven as usual.')
log.info('')
System.exit(0)