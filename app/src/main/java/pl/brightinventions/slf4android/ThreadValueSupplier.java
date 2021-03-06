package pl.brightinventions.slf4android;

class ThreadValueSupplier implements LoggerPatternValueSupplier {
    @Override
    public void append(LogRecord record, StringBuilder builder) {
        builder.append(record.getThreadName());
    }
}
