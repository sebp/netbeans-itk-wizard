#ifndef __itk${className}_h
#define __itk${className}_h

#include <itk${parentClassName}.h>

namespace itk
{
template<class TInputImage, class TOutputImage>
class ${className} : public ${parentClassName}<TInputImage, TOutputImage>
{
public:
    /** Standard class typedefs. */
    typedef ${className} Self;
    typedef ${parentClassName}<TInputImage, TOutputImage> Superclass;
    typedef SmartPointer<Self> Pointer;

    /** Run-time type information (and related methods). */
    itkTypeMacro(${className}, ${parentClassName});

    /** input image typedefs */
    typedef TInputImage InputImageType;
    typedef Superclass::InputImageConstPointer InputImageConstPointer;
    typedef typename InputImageType::RegionType InputImageRegionType;
    typedef typename InputImageType::PixelType InputImagePixelType;

    /** output image typedefs */
    typedef TOutputImage OutputImageType;
    typedef Superclass::OutputImagePointer OutputImagePointer;
    typedef Superclass::OutputImageRegionType OutputImageRegionType;
    typedef typename Superclass::OutputImagePixelType OutputImagePixelType;

    /** ImageDimension constants */
    itkStaticConstMacro(InputImageDimension, unsigned int,
                        TInputImage::ImageDimension);
    itkStaticConstMacro(OutputImageDimension, unsigned int,
                        TOutputImage::ImageDimension);

    /** Method for creation through the object factory. */
    itkNewMacro(Self);

protected:
    ${className}();
    virtual ~${className}() {};

    virtual void PrintSelf(std::ostream & os, Indent indent) const;

<#if multiThreaded>
    virtual void ThreadedGenerateData(const OutputImageRegionType& outputRegion, ThreadIdType threadId);
<#else>
    virtual void GenerateData();
</#if>

private:
    ${className}(const Self &); //purposely not implemented
    void operator=(const Self &); //purposely not implemented
};
}

#include "${className}.hxx"

#endif /* __itk${className}_h */
